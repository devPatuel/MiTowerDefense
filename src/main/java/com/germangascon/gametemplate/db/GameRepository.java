package com.germangascon.gametemplate.db;

import com.germangascon.gametemplate.core.GameContext;
import com.germangascon.gametemplate.entities.Entity;
import com.germangascon.gametemplate.entities.WaypointEntity;
import com.germangascon.gametemplate.game.EntityFactory;
import com.germangascon.gametemplate.game.GameState;
import com.germangascon.gametemplate.game.entities.SnowTower;
import com.germangascon.gametemplate.game.entities.Tower;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class GameRepository {
    private final MongoCollection<Document> collection;
    private final GameContext gameContext;

    public GameRepository(GameContext gameContext) {
        this.gameContext = gameContext;
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("savegames");
    }

    public void saveGame() {
        GameState state = GameState.getInstance();
        Document doc = new Document();
        // Usamos un ID fijo "player1" para simplificar (solo 1 slot de guardado)
        doc.append("_id", "player1");
        doc.append("money", state.getMoney());
        doc.append("lives", state.getLives());
        doc.append("wave", state.getWave());

        List<Document> towersList = new ArrayList<>();
        // Buscamos todas las torres (Básicas y de Nieve)
        Iterable<Entity> towers = gameContext.getEntitiesByCondition(Entity.class, 
            e -> e instanceof Tower || e instanceof SnowTower);

        for (Entity e : towers) {
            Document towerDoc = new Document();
            towerDoc.append("x", e.getX());
            towerDoc.append("y", e.getY());
            
            if (e instanceof SnowTower) {
                towerDoc.append("type", "snow");
                towerDoc.append("level", ((SnowTower) e).getLevel());
            } else {
                towerDoc.append("type", "basic");
                towerDoc.append("level", ((Tower) e).getLevel());
            }
            towersList.add(towerDoc);
        }
        doc.append("towers", towersList);

        // upsert(true) crea el documento si no existe, o lo reemplaza si existe
        collection.replaceOne(Filters.eq("_id", "player1"), doc, new ReplaceOptions().upsert(true));
        System.out.println("Partida guardada en MongoDB.");
    }

    public void loadGame() {
        Document doc = collection.find(Filters.eq("_id", "player1")).first();
        if (doc == null) {
            System.out.println("No se encontró ninguna partida guardada.");
            return;
        }

        // 1. Restaurar Estado Global
        GameState state = GameState.getInstance();
        state.setMoney(doc.getInteger("money", 100));
        state.setLives(doc.getInteger("lives", 20));
        state.setWave(doc.getInteger("wave", 1));

        // 2. Limpiar el mapa (Torres y Enemigos)
        // WaypointEntity cubre a Grinch, Santa y Tank
        Iterable<Entity> entitiesToRemove = gameContext.getEntitiesByCondition(Entity.class, 
            e -> e instanceof Tower || e instanceof SnowTower || e instanceof WaypointEntity);
        
        for (Entity e : entitiesToRemove) {
            e.destroy();
        }

        // 3. Recrear torres desde la BBDD
        List<Document> towersList = doc.getList("towers", Document.class);
        if (towersList != null) {
            EntityFactory factory = gameContext.getEntityFactory();
            for (Document towerDoc : towersList) {
                // MongoDB puede devolver Double, casteamos a float
                float x = towerDoc.getDouble("x").floatValue();
                float y = towerDoc.getDouble("y").floatValue();
                String type = towerDoc.getString("type");
                int level = towerDoc.getInteger("level", 1);

                if ("snow".equals(type)) {
                    factory.spawnSnowTower(x, y, level);
                } else {
                    factory.spawnTower(x, y, level);
                }
            }
        }
        System.out.println("Partida cargada desde MongoDB.");
    }
}
