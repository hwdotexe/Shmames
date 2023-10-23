package com.hadenwatne.botcore.storage;

import com.hadenwatne.botcore.App;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseService {
    private MongoDatabase _mongoDatabase;

    public DatabaseService(String connectionString, String databaseName) {
        try (MongoClient client = MongoClients.create(connectionString)) {
            _mongoDatabase = client.getDatabase(databaseName);
        } catch (Exception e) {
            _mongoDatabase = null;
            App.getLogger().LogException(e);
        }
    }

    public MongoDatabase getDatabase() {
       return this._mongoDatabase;
    }

    // TODO: need to add methods to CRUD rows.
}
