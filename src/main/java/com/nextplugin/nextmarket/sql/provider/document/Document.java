package com.nextplugin.nextmarket.sql.provider.document;

import org.apache.commons.lang.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;

public class Document {

    private final Map<String, Object> valueMap = new HashMap<>();

    public void insert(String key, Object value) {
        this.valueMap.put(key, value);
    }

    public String getString(String key) {
        Object result = this.valueMap.get(key);
        return result != null ? String.valueOf(result) : null;
    }

    public Number getNumber(String key) {
        return NumberUtils.createNumber(this.getString(key));
    }

    public <T> T deserialize(Serializer<T> serializer) {
        return serializer.deserialize(this);
    }

    public Map<String, Object> asMap() {
        return valueMap;
    }

}
