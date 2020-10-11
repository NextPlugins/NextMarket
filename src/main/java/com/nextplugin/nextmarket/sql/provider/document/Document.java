package com.nextplugin.nextmarket.sql.provider.document;

import com.nextplugin.nextmarket.sql.provider.document.parser.DocumentParser;
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

    public <T> T parse(DocumentParser<T> documentParser) {
        return documentParser.parse(this);
    }

}
