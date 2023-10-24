package com.hadenwatne.botcore.storage;

import com.hadenwatne.botcore.App;
import com.hadenwatne.botcore.type.LogType;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DatabaseService {
    private MongoDatabase _mongoDatabase;

    public DatabaseService(String connectionString, String databaseName) {
        App.getLogger().Log(LogType.SYSTEM, "Initializing database...");

        try (MongoClient client = MongoClients.create(connectionString)) {
            CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
            CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    pojoCodecRegistry);

            _mongoDatabase = client.getDatabase(databaseName).withCodecRegistry(codecRegistry);
        } catch (Exception e) {
            _mongoDatabase = null;
            App.getLogger().LogException(e);
        }

        App.getLogger().Log(LogType.SYSTEM, "Finished initializing database.");
    }

    public MongoDatabase getDatabase() {
       return this._mongoDatabase;
    }

    public <T> MongoCursor<T> readRows(Class<T> type, String table, String field, String value) {
        MongoCollection<T> collection = this._mongoDatabase.getCollection(table, type);
        Bson filter = eq(field, value);

        return collection.find(filter).cursor();
    }

    public <T> MongoCursor<T> readRows(Class<T> type, String table, Bson filter) {
        MongoCollection<T> collection = this._mongoDatabase.getCollection(table, type);

        return collection.find(filter).cursor();
    }

    public <T> MongoCursor<T> readTable(Class<T> type, String table) {
        MongoCollection<T> collection = this._mongoDatabase.getCollection(table, type);

        return collection.find().cursor();
    }

    public <T> void insertRecord(Class<T> type, String table, T record) {
        MongoCollection<T> collection = this._mongoDatabase.getCollection(table, type);

        collection.insertOne(record);
    }

    public <T> void updateRecord(Class<T> type, String table, String field, String value, T record) {
        MongoCollection<T> collection = this._mongoDatabase.getCollection(table, type);
        Bson filter = eq(field, value);

        collection.replaceOne(filter, record);
    }

    public <T> void updateRecord(Class<T> type, String table, Bson filter, T record) {
        MongoCollection<T> collection = this._mongoDatabase.getCollection(table, type);

        collection.replaceOne(filter, record);
    }

    public <T> void deleteRecord(Class<T> type, String table, String field, String value) {
        MongoCollection<T> collection = this._mongoDatabase.getCollection(table, type);
        Bson filter = eq(field, value);

        collection.deleteOne(filter);
    }
}
