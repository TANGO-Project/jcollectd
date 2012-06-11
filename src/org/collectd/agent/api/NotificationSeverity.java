package org.collectd.agent.api;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum NotificationSeverity {
    FAILURE(1),
    WARNING(2),
    OKAY(4),
    UNKNOWN(3);

    private static final Map<Integer, NotificationSeverity> lookup
            = new HashMap<Integer, NotificationSeverity>();
    private static final String[] names = new String[names().length];

    static {
        int i = 0;
        for (NotificationSeverity s : EnumSet.allOf(NotificationSeverity.class)) {
            lookup.put(s.serverity, s);
            names[i]=s.name();
            i++;
        }
    }

    public static String[] names() {
        return names;
    }

    public static NotificationSeverity get(int severity) {
        return lookup.get(severity);
    }

    public final int serverity;

    NotificationSeverity(int severity) {
        this.serverity = severity;
    }

}