package com.nextplugin.nextmarket.sql.provider;

import com.google.inject.Inject;
import com.nextplugin.nextmarket.sql.connection.SQLConnection;
import com.nextplugin.nextmarket.sql.provider.document.Document;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseProvider {

    @Inject private SQLConnection sqlConnection;

    public List<Document> queryMany(String query, Object... values) {
        List<Document> documents = new ArrayList<>();

        try (PreparedStatement statement = sqlConnection.findConnection().prepareStatement(query)) {
            for (int index = 0; index < values.length; index++) {
                statement.setObject(index + 1, values[index]);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData resultMetaData = resultSet.getMetaData();

                while (resultSet.next()) {
                    Document document = new Document();
                    for (int index = 1; index <= resultMetaData.getColumnCount(); index++) {
                        String name = resultMetaData.getColumnName(index);
                        document.insert(name, resultSet.getObject(index));
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
