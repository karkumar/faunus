package com.thinkaurelius.faunus;

import com.tinkerpop.blueprints.Element;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class FaunusElement implements Element, Writable {

    protected Map<String, Object> properties = new HashMap<String, Object>();
    protected long id;

    public FaunusElement(final Long id) {
        this.id = id;
    }

    public void setProperty(final String key, final Object value) {
        this.properties.put(key, value);
    }

    public Object removeProperty(final String key) {
        return this.properties.remove(key);
    }

    public Object getProperty(final String key) {
        return this.properties.get(key);
    }

    public Set<String> getPropertyKeys() {
        return this.properties.keySet();
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void setProperties(final Map<String, Object> properties) {
        this.properties.clear();
        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            this.properties.put(entry.getKey(), entry.getValue());
        }
    }

    public Object getId() {
        return this.id;
    }

    public Long getIdAsLong() {
        return this.id;
    }

    public LongWritable getIdAsLongWritable() {
        return new LongWritable(this.id);
    }

    @Override
    public boolean equals(final Object other) {
        return this.getClass().equals(other.getClass()) && this.id == (Long) ((FaunusElement) other).getId();
    }

    @Override
    public int hashCode() {
        return ((Long) this.id).hashCode();
    }
}