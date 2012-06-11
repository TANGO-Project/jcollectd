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

package org.collectd.common.mx;

import junit.framework.TestCase;
import org.collectd.agent.mx.MBeanCollector;
import org.collectd.agent.mx.MBeanConfig;
import org.collectd.agent.mx.MBeanQuery;

import java.io.File;

public class MBeanConfigTest extends TestCase {

    //etc/java-jcollectd.xml
    public void testJavaLang() throws Exception {
        MBeanConfig config = new MBeanConfig();
        MBeanCollector collector = config.add("java");
        assertNotNull(collector);
        assertEquals(collector.getQueries().size(), 4);
    }

    public void testJavaLangOS() throws Exception {
        MBeanConfig config = new MBeanConfig();
        String name = "java.lang:type=OperatingSystem";
        MBeanCollector collector = config.add(name);
        assertNull(collector);
        collector = new MBeanCollector();
        MBeanQuery query = collector.addMBean(name);
        assertNotNull(query);
    }

    public void testInvalid() throws Exception {
        final String file = "build.xml";
        if (new File(file).exists()) {
            try {
                new MBeanConfig().add(file);
                assertTrue(false);
            } catch (Exception e) {
                assertTrue(true);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            MBeanConfig config = new MBeanConfig();
            MBeanCollector collector = config.add(arg);
            if (collector == null) {
                System.out.println(arg + "...NOT FOUND");
            } else {
                System.out.println(arg + "..." +
                        collector.getQueries().size());
            }
        }
    }
}
