package com.nextplugin.nextmarket.sql.provider.document.parser;

import com.nextplugin.nextmarket.sql.provider.document.Document;

public interface DocumentParser<T> {

    T parse(Document document);

}
