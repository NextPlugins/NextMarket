package com.nextplugin.nextmarket.sql.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nextplugin.nextmarket.NextMarket;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.SQLException;

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
