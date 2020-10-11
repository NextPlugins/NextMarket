package com.nextplugin.nextmarket.sql.connection;

import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
public interface SQLConnection {

//    private final HikariDataSource dataSource = new HikariDataSource();


//    public Connection findConnection() {
//        try {
//            return this.dataSource.isRunning() ? this.dataSource.getConnection() : this.connection;
//        } catch (SQLException t) {
//            t.printStackTrace();
//            Bukkit.getPluginManager().disablePlugin(this.nextMarket);
//            return null;
//        }
//    }

    boolean configure(ConfigurationSection section);

    Connection findConnection();

}
