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

package org.jcollectd.agent.protocol;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jcollectd.agent.api.Notification;
import org.jcollectd.agent.api.PacketBuilder;
import org.jcollectd.agent.api.Values;
import org.jcollectd.server.protocol.ReceiverTest;
import org.jcollectd.server.protocol.UdpReceiver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SenderTest
        extends TestCase
        implements Dispatcher {

    private static final long INTERVAL = 10;
    private static final String PLUGIN = "junit";
    private static final String PLUGIN_INSTANCE = "SenderTest";
    private static final String TYPE = "test";

    private final double[] dvals = {1.0, 66.77, Double.MAX_VALUE};
    private final long[] lvals = {1, 66, Long.MAX_VALUE, 4};

    private final List<Values> _values = new ArrayList<Values>();
    private Sender _sender;
    private ReceiverTest _receiverTest;

    public static Test suite() {
        return new TestSuite(SenderTest.class);
    }

    Logger getLog() {
        return Logger.getLogger(getClass().getName());
    }

    protected ReceiverTest createReceiverTest() {
        ReceiverTest rtest = new ReceiverTest();
        return rtest;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _receiverTest = createReceiverTest();
        _receiverTest.setUp();
        UdpReceiver receiver = _receiverTest.getReceiver();
        receiver.setDispatcher(this);
        int port = receiver.getSocket().getLocalPort();
        _sender = new UdpSender();
        String dest = receiver.getListenAddress() + ":" + port;
        getLog().info("Add destination: " + dest);
        _sender.addServer(dest);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _receiverTest.tearDown();
    }

    private Values newValueList() {
        Values vl = PacketBuilder.newInstance().plugin(PLUGIN).pluginInstance(PLUGIN_INSTANCE).interval(INTERVAL).type(TYPE).buildValues();
        return vl;
    }

    private void assertValueList(Values vl,
                                 String host, long time)
            throws Exception {

        assertEquals(vl.getHost(), host);
        assertEquals(vl.getTime() / 1000, time);
        assertEquals("GOT: " + INTERVAL + " EXPECTED: " + vl.getInterval(),
                vl.getInterval(), INTERVAL);
        assertEquals("GOT: " + PLUGIN + " EXPECTED: " + vl.getPlugin(),
                vl.getPlugin(), PLUGIN);
        assertEquals("GOT: " + PLUGIN_INSTANCE + "EXPECTED: " + vl.getPluginInstance(),
                PLUGIN_INSTANCE, vl.getPluginInstance());
        assertEquals("GOT: " + TYPE + "EXPECTED: " + vl.getPluginInstance(),
                vl.getType(), TYPE);
    }

    private void flush() throws Exception {
        _sender.flush();
        Thread.sleep(500);
    }

    public void testGauge() throws Exception {
        Values vl = newValueList();
        for (double val : dvals) {
            vl.addValue(val);
        }
        _sender.dispatch(vl);
        String host = vl.getHost();
        long time = vl.getTime() / 1000;
        flush();
        assertEquals(_values.size(), 1);
        vl = _values.get(0);
        assertValueList(vl, host, time);
        assertEquals(vl.getList().size(), dvals.length);
        int i = 0;
        for (Number num : vl.getList()) {
            assertEquals(num.getClass(), Double.class);
            assertEquals(num.doubleValue(), dvals[i++]);
        }
        _values.clear();
    }

    public void testCounter() throws Exception {
        Values vl = newValueList();
        for (long val : lvals) {
            vl.addValue(val);
        }
        _sender.dispatch(vl);
        String host = vl.getHost();
        long time = vl.getTime() / 1000;
        flush();
        assertEquals(_values.size(), 1);
        vl = _values.get(0);
        assertValueList(vl, host, time);
        assertEquals(vl.getList().size(), lvals.length);
        int i = 0;
        for (Number num : vl.getList()) {
            assertEquals(num.getClass(), Long.class);
            assertEquals(num.longValue(), lvals[i++]);
        }
        _values.clear();
    }

    public void dispatch(Notification notification) {

    }

    public void dispatch(Values vl) {
        _values.add(vl);
    }
}
