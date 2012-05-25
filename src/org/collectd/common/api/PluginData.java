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

package org.collectd.common.api;

import java.util.Date;

/**
 * Shared members of value_list_t and notification_t structures.
 */
public class PluginData {

    private long _time;
    private String _host;
    private String _plugin;
    private String _pluginInstance = "";
    private String _type = "";
    private String _typeInstance = "";

    public PluginData() {

    }

    PluginData(PluginData pd) {
        _time = pd._time;
        _host = pd._host;
        _plugin = strip(pd._plugin);
        _pluginInstance = strip(pd._pluginInstance);
        _type = pd._type;
        _typeInstance = pd._typeInstance;
    }

    private String strip(String string) {
        if (string != null)
            string = string.replaceAll("[\\s\"]+", "_");
        return string;
    }

    public long getTime() {
        return _time;
    }

    public void setTime(long time) {
        _time = time;
    }

    public String getHost() {
        return _host;
    }

    public void setHost(String host) {
        _host = host;
    }

    public String getPlugin() {
        return _plugin;
    }

    public void setPlugin(String plugin) {
        _plugin = strip(plugin);
    }

    public String getPluginInstance() {
        return _pluginInstance;
    }

    public void setPluginInstance(String pluginInstance) {
        _pluginInstance = strip(pluginInstance);
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public String getTypeInstance() {
        return _typeInstance;
    }

    public void setTypeInstance(String typeInstance) {
        _typeInstance = typeInstance;
    }

    public boolean defined(String val) {
        return (val != null) && (val.length() > 0);
    }

    public String getSource() {
        StringBuffer sb = new StringBuffer();
        appendToSource(sb, _host);
        appendToSource(sb, _plugin);
        appendToSource(sb, _pluginInstance);
        appendToSource(sb, _type);
        appendToSource(sb, _typeInstance);
        return sb.toString();
    }

    private void appendToSource(StringBuffer sb, String value) {
        if (defined(value)) {
            if(sb.length() != 0){
                sb.append('/');
            }
            sb.append(value);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(new Date(_time)).append("] ");
        sb.append(getSource());
        return sb.toString();
    }
}
