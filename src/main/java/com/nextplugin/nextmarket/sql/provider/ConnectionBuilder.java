package com.nextplugin.nextmarket.sql.provider;

import java.sql.Connection;

public class ConnectionBuilder {

    private String driver;
    private String url;
    private String user;
    private String password;
    
    public static ConnectionBuilder builder() {
        return new ConnectionBuilder();
    }

    public ConnectionBuilder driver(String driver) {
        this.driver = driver;

        return this;
    }

    public ConnectionBuilder url(String url) {
        this.url = url;

        return this;
    }

    public ConnectionBuilder password(String password) {
        this.password = password;

        return this;
    }

    public ConnectionBuilder user(String user) {
        this.user = user;

        return this;
    }

    public Connection build() {
        return null;
    }
}
