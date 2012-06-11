/*
 * jcollectd
 * Copyright (C) 2009 Hyperic, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; only version 2 of the License is applicable.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */

package org.collectd.server.protocol;

import org.collectd.agent.api.PacketBuilder;
import org.collectd.agent.api.Type;
import org.collectd.agent.protocol.Dispatcher;
import org.collectd.agent.protocol.Network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Logger;

/**
 * collectd UDP protocol receiver.
 * See collectd/src/network.c:parse_packet
 */
public class UdpReceiver {

    private static final Logger _log =
            Logger.getLogger(UdpReceiver.class.getName());
    private Dispatcher _dispatcher;
    private DatagramSocket _socket;
    private int _port = Network.DEFAULT_PORT;
    private String _bindAddress;
    private String _ifAddress;
    private boolean _isShutdown = false;

    public UdpReceiver() {
        String addr = Network.getProperty("laddr", Network.DEFAULT_V4_ADDR);
        if (addr != null) {
            int ix = addr.indexOf(':'); //XXX ipv6
            if (ix == -1) {
                _bindAddress = addr;
            } else {
                _bindAddress = addr.substring(0, ix);
                _port = Integer.parseInt(addr.substring(ix + 1));
            }
        }
        addr = Network.getProperty("ifaddr");
        if (addr != null) {
            try {
                //-Djcd.ifaddr=tun0
                _ifAddress =
                        NetworkInterface.getByName(addr).getInetAddresses().
                                nextElement().getHostAddress();
            } catch (Exception e) {
                //-Djcd.ifaddr=10.2.0.43
                _ifAddress = addr;
            }
            _log.fine("Using interface address=" + _ifAddress);
        }
    }

    private UdpReceiver(Dispatcher dispatcher) {
        this();
        setDispatcher(dispatcher);
    }

    public void setDispatcher(Dispatcher dispatcher) {
        _dispatcher = dispatcher;
    }

    protected int getPort() {
        return _port;
    }

    public void setPort(int port) {
        _port = port;
    }

    public String getListenAddress() {
        return _bindAddress;
    }

    public void setListenAddress(String address) {
        _bindAddress = address;
    }

    public String getInterfaceAddress() {
        return _ifAddress;
    }

    public void setInterfaceAddress(String address) {
        _ifAddress = address;
    }

    public DatagramSocket getSocket() throws IOException {
        if (_socket == null) {
            if (_bindAddress == null) {
                _socket = new DatagramSocket(_port);
            } else {
                InetAddress addr = InetAddress.getByName(_bindAddress);
                if (addr.isMulticastAddress()) {
                    MulticastSocket mcast = new MulticastSocket(_port);
                    if (_ifAddress != null) {
                        mcast.setInterface(InetAddress.getByName(_ifAddress));
                    }
                    mcast.joinGroup(addr);
                    _socket = mcast;
                } else {
                    _socket = new DatagramSocket(_port, addr);
                }
            }
        }
        return _socket;
    }

    public void setSocket(DatagramSocket socket) {
        _socket = socket;
    }

    private String readString(DataInputStream is, int len)
            throws IOException {
        byte[] buf = new byte[len];
        is.read(buf, 0, len);
        return new String(buf, 0, len - 1); //-1 -> skip \0
    }

    private void readValues(DataInputStream is, PacketBuilder vl)
            throws IOException {
        byte[] dbuff = new byte[8];
        int nvalues = is.readUnsignedShort();
        int[] types = new int[nvalues];
        for (int i = 0; i < nvalues; i++) {
            types[i] = is.readByte();
        }
        for (int i = 0; i < nvalues; i++) {
            Number val;
            if (types[i] == Type.COUNTER.value) {
                val = is.readLong();
            } else {
                //collectd uses x86 host order for doubles
                is.read(dbuff);
                ByteBuffer bb = ByteBuffer.wrap(dbuff);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                val = bb.getDouble();
            }
            vl.addValue(val);
        }
        if (_dispatcher != null) {
            _dispatcher.dispatch(vl.buildValues());
        }
    }

    public void parse(byte[] packet) throws IOException {
        int total = packet.length;
        ByteArrayInputStream buffer =
                new ByteArrayInputStream(packet);
        DataInputStream is =
                new DataInputStream(buffer);
        PacketBuilder dataBuilder = PacketBuilder.newInstance();

        while ((0 < total) && (total > Network.HEADER_LEN)) {
            int type = is.readUnsignedShort();
            int len = is.readUnsignedShort();

            if (len < Network.HEADER_LEN) {
                break; //packet was filled to the brim
            }

            total -= len;
            len -= Network.HEADER_LEN;

            switch (type) {
                case Network.TYPE_VALUES:
                    readValues(is, dataBuilder);
                    break;
                case Network.TYPE_TIME:
                    long tmp = is.readLong()*1000;
                    dataBuilder.time(tmp);
                    break;
                case Network.TYPE_INTERVAL:
                    long interval = is.readLong();
                    dataBuilder.interval(interval);
                    break;
                case Network.TYPE_TIME_HIRES:
                    long thi = is.readLong()*1000;
                    dataBuilder.time(thi);
                    break;
                case Network.TYPE_INTERVAL_HIRES:
                    long ihi = is.readLong();
                    dataBuilder.interval(ihi);
                    break;
                case Network.TYPE_HOST:
                    String host = readString(is, len);
                    dataBuilder.host(host);
                    break;
                case Network.TYPE_PLUGIN:
                    String plugin = readString(is, len);
                    dataBuilder.plugin(plugin);
                    break;
                case Network.TYPE_PLUGIN_INSTANCE:
                    String pluginInstance = readString(is, len);
                    dataBuilder.pluginInstance(pluginInstance);
                    break;
                case Network.TYPE_TYPE:
                    String _type = readString(is, len);
                    dataBuilder.type(_type);
                    break;
                case Network.TYPE_TYPE_INSTANCE:
                    String tI = readString(is, len);
                    dataBuilder.typeInstance(tI);
                    break;
                case Network.TYPE_MESSAGE:
                    String msg = readString(is, len);
                    dataBuilder.message(msg);
                    if (_dispatcher != null) {
                        _dispatcher.dispatch(dataBuilder.buildNotification());
                    }
                    break;
                case Network.TYPE_SEVERITY:
                    int sev = (int) is.readLong();
                    dataBuilder.severity(sev);
                    break;
                default:
                    break;
            }
        }
    }

    public void listen() throws Exception {
        listen(getSocket());
    }

    void listen(DatagramSocket socket) throws IOException {
        while (true) {
            byte[] buf = new byte[Network.BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (SocketException e) {
                if (_isShutdown) {
                    break;
                } else {
                    throw e;
                }
            }
            parse(packet.getData());
        }
    }

    public void shutdown() {
        if (_socket != null) {
            _isShutdown = true;
            _socket.close();
            _socket = null;
        }
    }

    public static void main(String[] args) throws Exception {
        new UdpReceiver(new StdoutDispatcher()).listen();
    }
}
