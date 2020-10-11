package com.nextplugin.nextmarket.sql.provider;

import com.google.inject.Inject;
import com.nextplugin.nextmarket.sql.connection.SQLConnection;
import com.nextplugin.nextmarket.sql.provider.document.Document;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProvider {

    @Inject private SQLConnection sqlConnection;

    public Document queryOne(String query, Object... values) {
        try (PreparedStatement statement = sqlConnection.findConnection().prepareStatement(query)) {
            for (int index = 0; index < values.length; index++) {
                statement.setObject(index + 1, values[index]);
            }

            try (ResultSet set = statement.executeQuery()) {
                ResultSetMetaData setMetaData = set.getMetaData();

                if (set.next()) {
                    Document document = new Document();

                    for (int index = 1; index <= setMetaData.getColumnCount(); index++) {
                        String name = setMetaData.getColumnName(index);

                        document.insert(name, set.getObject(index));
                    }

                    return document;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Document();
    }

    public List<Document> queryMany(String query, Object... values) {
        List<Document> documents = new ArrayList<>();

        try (PreparedStatement statement = sqlConnection.findConnection().prepareStatement(query)) {
            for (int index = 0; index < values.length; index++) {
                statement.setObject(index + 1, values[index]);
            }

            try (ResultSet set = statement.executeQuery()) {
                ResultSetMetaData setMetaData = set.getMetaData();

                while (set.next()) {
                    Document document = new Document();

                    for (int index = 1; index <= setMetaData.getColumnCount(); index++) {
                        String name = setMetaData.getColumnName(index);

                        System.out.println("insert: " + name + " with: " + set.getObject(index));
                        document.insert(name, set.getObject(index));
                    }

                    documents.add(document);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return documents;
    }

    public void update(String query, Object... values) {
        try (PreparedStatement statement = sqlConnection.findConnection().prepareStatement(query)) {
            for (int index = 0; index < values.length; index++) {
                statement.setObject(index + 1, values[index]);
            }

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
