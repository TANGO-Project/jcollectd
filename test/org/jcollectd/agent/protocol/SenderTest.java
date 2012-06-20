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
import org.jcollectd.agent.api.Identifier;
import org.jcollectd.agent.api.Notification;
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
        Identifier identifier = Identifier.Builder.builder().plugin(PLUGIN).pluginInstance(PLUGIN_INSTANCE).type(TYPE).build();
        Values vl = new Values(identifier);
        vl.setInterval(INTERVAL);
        return vl;
    }

    private void assertValueList(Values vals,
                                 String host, long time)
            throws Exception {
        assertEquals(vals.getHost(), host);
        assertEquals(vals.getTime() / 1000, time);
        assertEquals("GOT: " + INTERVAL + " EXPECTED: " + vals.getInterval(),
                vals.getInterval(), INTERVAL);
        assertEquals("GOT: " + PLUGIN + " EXPECTED: " + vals.getPlugin(),
                vals.getPlugin(), PLUGIN);
        assertEquals("GOT: " + PLUGIN_INSTANCE + "EXPECTED: " + vals.getPluginInstance(),
                PLUGIN_INSTANCE, vals.getPluginInstance());
        assertEquals("GOT: " + TYPE + "EXPECTED: " + vals.getPluginInstance(),
                vals.getType(), TYPE);
    }

    private void flush() throws Exception {
        _sender.flush();
        Thread.sleep(500);
    }

    public void testGauge() throws Exception {
        Values values = newValueList();
        for (double val : dvals) {
            values.addValue(val);
        }

        _sender.dispatch(values);
        String host = values.getHost();
        long time = values.getTime() / 1000;
        flush();
        assertEquals(_values.size(), 1);
        values = _values.get(0);
        assertValueList(values, host, time);
        assertEquals(values.getData().size(), dvals.length);
        int i = 0;
        for (Number num : values.getData()) {
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
        assertEquals(vl.getData().size(), lvals.length);
        int i = 0;
        for (Number num : vl.getData()) {
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
