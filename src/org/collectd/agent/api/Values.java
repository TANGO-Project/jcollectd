package org.collectd.agent.api;

import java.util.ArrayList;
import java.util.List;

public class Values extends Identifier{
    private List<Number> _values = new ArrayList<Number>();
    private List<DataSource> _ds = new ArrayList<DataSource>();
    private long interval;

    Values(Identifier identifier) {
        super(identifier);
    }

    Values(Identifier identifier, long interval, List<Number> values) {
        this(identifier);
        this.interval = interval;
        _values = values;
    }

    public void addValue(Number value) {
        _values.add(value);
    }

    public List<DataSource> getDataSource() {
        if (_ds.size() > 0) {
            return _ds;
        } else {
            return null;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("=[");
        List<DataSource> ds = getDataSource();
        int size = _values.size();
        for (int i = 0; i < size; i++) {
            Number val = _values.get(i);
            String name;
            if (ds == null) {
                name = "unknown" + i;
            } else {
                name = ds.get(i).getName();
            }
            sb.append(name).append('=').append(val);
            if (i < size - 1) {
                sb.append(',');
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public List<Number> getList() {
        return _values;
    }

    public long getInterval() {
        return interval;
    }

    public void clearValues() {
            _values.clear();
    }
}
