package com.nextplugin.nextmarket.sql.connection.sqlite;

import com.nextplugin.nextmarket.NextMarket;
import com.nextplugin.nextmarket.sql.connection.SQLConnection;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Henry Fábio
 * Github: https://github.com/HenryFabio
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public final class SQLiteConnection implements SQLConnection {

    private File sqlFile;
    private Connection connection;

    @SneakyThrows
    @Override
    public boolean configure(ConfigurationSection section) {
        NextMarket nextMarket = NextMarket.getInstance();
        this.sqlFile = createSQLFile(nextMarket.getDataFolder(), section.getString("file"));
        return true;
    }

    @Override
    public Connection findConnection() {
        if (connection == null) {
            try {
                Class.forName("org.sqlite.JDBC");
                this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.sqlFile);
            } catch (Throwable t) {
                t.printStackTrace();
                Bukkit.getPluginManager().disablePlugin(NextMarket.getInstance());
            }
        }

        return this.connection;
    }

    @SneakyThrows
    private File createSQLFile(File dataFolder, String name) {
        File file = new File(dataFolder + File.separator + "sql", name);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            parentFile.mkdirs();

            file.createNewFile();
        }
        return file;
    }

}
