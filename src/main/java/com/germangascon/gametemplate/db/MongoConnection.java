package com.germangascon.gametemplate.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {
        if (database == null) {
            try {
                // Conexión a localhost por defecto. Cambia la URI si usas Atlas.
                mongoClient = MongoClients.create("mongodb://localhost:27017");
                database = mongoClient.getDatabase("towerdefense");
            } catch (Exception e) {
                System.err.println("Error conectando a MongoDB: " + e.getMessage());
                throw e;
            }
        }
        return database;
    }

    public static void close() {
        if (mongoClient != null) mongoClient.close();
    }
}