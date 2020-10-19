package com.nextplugin.nextmarket.configuration;

import java.util.List;

public interface ConfigImplementation {

    String translateColor(String key);

    String translateMessage(String key);

    List<String> translateMessageList(String key);

}
