package org.collectd.agent.api;

public enum Type {
    COUNTER(0),
    GAUGE(1),
    DERIVE(2),
    ABSOLUTE(3);

    public final int value;

    Type(int i) {
        value = i;
    }
}
