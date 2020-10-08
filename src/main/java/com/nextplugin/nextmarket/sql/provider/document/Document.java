package com.nextplugin.nextmarket.sql.provider.document;

import java.util.HashMap;
import java.util.Map;

public class Document {

    private final Map<String, Object> values = new HashMap<>();

    public Map<String, Object> asMap() {
        return values;
    }

    public Document insert(String key, Object value) {
        asMap().put(key, value);

        return this;
    }

    public Object getObject(String key) {
        return asMap().get(key);
    }

    public String getString(String key) {
        return getObject(key).toString();
    }

    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public boolean is(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(getString(key));
    }

    public float getFloat(String key) {
        return Float.parseFloat(getString(key));
    }

    public <T> T get(Serializer<T> serializer, String key) {
        return serializer.deserialize(getString(key));
    }

}
