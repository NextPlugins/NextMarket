package com.nextplugin.nextmarket.sql.provider.document;

public interface Serializer<T> {

    String serialize(T value);
    T deserialize(String value);

}
