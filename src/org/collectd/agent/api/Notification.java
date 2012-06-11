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

package org.collectd.agent.api;

/**
 * Java representation of collectd/src/plugin.h:notfication_t structure.
 */
public class Notification extends Identifier {
    private NotificationSeverity _severity;
    private String _message;

    Notification(Identifier identifier, int severity, String message) {
        super(identifier);
        _severity = NotificationSeverity.get(severity);
        _message = message;
    }

    public String getSeverityString() {
        if(_severity != null){
            return _severity.name();
        }
        return NotificationSeverity.UNKNOWN.name();
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

}
