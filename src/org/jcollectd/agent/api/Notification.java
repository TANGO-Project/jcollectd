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

package org.jcollectd.agent.api;

/**
 * Java representation of collectd/src/plugin.h:notfication_t structure.
 */
public class Notification extends Identifier {
    private Severity _severity;
    private String _message;

    public Notification(Identifier build, Severity severity, String message) {
        super(build);
        _severity = severity;
        _message = message;
    }

    public String getSeverityString() {
        if (_severity != null) {
            return _severity.name();
        }
        return Severity.UNKNOWN.name();
    }

    public String getMessage() {
        return _message;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" [").append(getSeverityString()).append("] ");
        sb.append(_message);
        return sb.toString();
    }

    public enum Severity {
        FAILURE(1),
        WARNING(2),
        UNKNOWN(3),
        OKAY(4);

        private static final Severity[] lookup = {UNKNOWN, FAILURE, WARNING, UNKNOWN, OKAY};
        private static final String[] names = {FAILURE.name(), WARNING.name(), UNKNOWN.name(), OKAY.name()};

        public static String[] names() {
            return names;
        }

        public static Severity find(int severity) {
            if (severity > 0 && severity < lookup.length) {
                return lookup[severity];
            }
            return UNKNOWN;
        }

        public final int id;

        Severity(int severity) {
            this.id = severity;
        }

    }

}
