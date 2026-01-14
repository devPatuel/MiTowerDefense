package com.germangascon.gametemplate.game.entities;

import com.germangascon.gametemplate.core.CollisionLayer;
import com.germangascon.gametemplate.entities.Entity;
import com.germangascon.gametemplate.entities.WaypointEntity;
import com.germangascon.gametemplate.math.Vector2;

import java.util.List;

/**
 * <p><strong>Grinch</strong></p>
 * <p>Entidad enemiga tipo Grinch</p>
 */
public class Grinch extends WaypointEntity {
    private int reward = 10; // Monedas que da al morir

    public Grinch(float x, float y, int hp, int damage, float velocity, List<Vector2> waypoints) {
        // Asumimos dimensiones similares al Tank por ahora (42x42), ajusta si la imagen es diferente
        super(x, y, 56, 56, 50, 40, hp, damage, "/img/grinch.png", velocity, waypoints);
        
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