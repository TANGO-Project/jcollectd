package org.collectd.agent.api;

import java.util.Date;

public class Identifier {

    long _time;
    String _host;
    String _plugin;
    String _pluginInstance = "";
    String _type = "";
    String _typeInstance = "";

    Identifier() {
    }

    Identifier(Identifier identifier) {
        _time = identifier._time;
        _host = identifier._host;
        _plugin = identifier._plugin;
        _pluginInstance = identifier._pluginInstance;
        _type = identifier._type;
        _typeInstance = identifier._typeInstance;
    }

    public long getTime() {
        return _time;
    }

    public String getHost() {
        return _host;
    }

    public String getPlugin() {
        return _plugin;
    }

    public String getPluginInstance() {
        return _pluginInstance;
    }

    public String getType() {
        return _type;
    }

    public String getTypeInstance() {
        return _typeInstance;
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
            if (sb.length() != 0) {
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
