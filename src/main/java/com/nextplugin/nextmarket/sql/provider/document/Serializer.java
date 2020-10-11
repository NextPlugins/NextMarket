package com.nextplugin.nextmarket.sql.provider.document;

import java.util.Map;

public interface Serializer<T> {

    String serialize(T value);
    T deserialize(Document document);

}
