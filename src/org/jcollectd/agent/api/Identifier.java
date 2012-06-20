package org.jcollectd.agent.api;

import java.util.Date;

public class Identifier {

    private long time;
    private String host;
    private String plugin;
    private String pluginInstance;
    private String type;
    private String typeInstance;

    private Identifier() {
    }

    public long getTime() {
        return time;
    }

    public String getHost() {
        return host;
    }

    public String getPlugin() {
        return plugin;
    }

    public String getPluginInstance() {
        return pluginInstance;
    }

    public String getType() {
        return type;
    }

    public String getTypeInstance() {
        return typeInstance;
    }


    public boolean defined(String val) {
        return (val != null) && (val.length() > 0);
    }

    public String getSource() {
        StringBuffer sb = new StringBuffer();
        appendToSource(sb, host);
        appendToSource(sb, plugin);
        appendToSource(sb, pluginInstance);
        appendToSource(sb, type);
        appendToSource(sb, typeInstance);
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
        sb.append('[').append(new Date(time)).append("] ");
        sb.append(getSource());
        return sb.toString();
    }

    public static class Builder {
        private long time;
        private String host;
        private String plugin;
        private String pluginInstance;
        private String type;
        private String typeInstance;

        public Builder() {
        }

        public Builder(Identifier identifier) {
            time = identifier.time;
            host = identifier.host;
            plugin = identifier.plugin;
            pluginInstance = identifier.pluginInstance;
            type = identifier.type;
            typeInstance = identifier.typeInstance;
        }

        public Builder time(long time) {
            this.time = time;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder plugin(String plugin) {
            this.plugin = strip(plugin);
            return this;
        }

        public Builder pluginInstance(String pluginInstance) {
            this.pluginInstance = strip(pluginInstance);
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder typeInstance(String typeInstance) {
            this.typeInstance = typeInstance;
            return this;
        }

        public Identifier build() {
            Identifier identifier = new Identifier();
            identifier.time = time;
            identifier.host = host;
            identifier.plugin = plugin;
            identifier.pluginInstance = pluginInstance;
            identifier.type = type;
            identifier.typeInstance = typeInstance;
            return identifier;
        }

        private String strip(String string) {
            if (string != null)
                string = string.replaceAll("[\\s\"]+", "_");
            return string;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static Builder builder(Identifier identifier) {
            return new Builder(identifier);
        }

    }

}
