package org.collectd.agent.api;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PacketBuilder {
    private long time;
    private long interval;
    private List<Number> values = new ArrayList<Number>();
    private String host;
    private String plugin;
    private String pluginInstance;
    private String type;
    private String typeInstance;
    private String message;
    private int severity;

    public static PacketBuilder newInstance() {
        return new PacketBuilder();
    }

    public PacketBuilder time(long time) {
        this.time = time;
        return this;
    }

    public PacketBuilder interval(long interval) {
        this.interval = interval;
        return this;
    }

    public PacketBuilder host(String host) {
        this.host = host;
        return this;
    }

    public PacketBuilder plugin(String plugin) {
        this.plugin = strip(plugin);
        return this;
    }

    public PacketBuilder pluginInstance(String pluginInstance) {
        this.pluginInstance = strip(pluginInstance);
        return this;
    }

    public PacketBuilder type(String type) {
        this.type = type;
        return this;
    }

    public PacketBuilder typeInstance(String typeInstance) {
        this.typeInstance = typeInstance;
        return this;
    }

    public PacketBuilder severity(int severity) {
        this.severity = severity;
        return this;
    }

    public Identifier build() {
        Identifier identifier = new Identifier();

        try {
            identifier._host = host != null ? host : InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            identifier._host = "UNKNOWN";
        }

        identifier._time = time > 0 ? time : System.currentTimeMillis();

        identifier._plugin = plugin;
        identifier._pluginInstance = pluginInstance;
        identifier._type = type;
        identifier._typeInstance = typeInstance;
        return identifier;
    }


    public Notification buildNotification() {
        return new Notification(build(), severity, message);
    }

    public Values buildValues() {
        return new Values(build(), interval, values);
    }

    private String strip(String string) {
        if (string != null)
            string = string.replaceAll("[\\s\"]+", "_");
        return string;
    }

    public void message(String message) {
        this.message = message;
    }

    public void addValue(Number val) {
        values.add(val);
    }
}