package com.germangascon.gametemplate.game.entities;

import com.germangascon.gametemplate.core.CollisionLayer;
import com.germangascon.gametemplate.entities.Entity;
import com.germangascon.gametemplate.entities.WaypointEntity;
import com.germangascon.gametemplate.math.Vector2;

import java.util.List;

/**
 * <p><strong>Santa</strong></p>
 * <p>Entidad enemiga tipo Santa</p>
 */
public class Santa extends WaypointEntity {
    private int reward = 25; // Santa da más recompensa

    public Santa(float x, float y, int hp, int damage, float velocity, List<Vector2> waypoints) {
        // Un poco más grande que el Grinch (48x48)
        super(x, y, 64, 64, 54, 54, hp, damage, "/img/santa.png", velocity, waypoints);
        
        // Configuración de capas de colisión
        setCollisionLayer(CollisionLayer.LAYER_ENEMY);
        addCollisionMask(CollisionLayer.LAYER_PLAYER);
        addCollisionMask(CollisionLayer.LAYER_ENEMY);
        addCollisionMask(CollisionLayer.LAYER_PLAYER_BULLET);
    }

    public int getReward() {
        return reward;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void preUpdate(double deltaTime) {

    }

    @Override
    public void postUpdate(double deltaTime) {

    }

    @Override
    public void lateUpdate(double deltaTime) {

    }

    @Override
    public void onCollision(Entity entity) {

    }
}