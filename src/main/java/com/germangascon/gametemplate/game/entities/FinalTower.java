package com.germangascon.gametemplate.game.entities;

import com.germangascon.gametemplate.core.Timer;
import com.germangascon.gametemplate.entities.Entity;
import com.germangascon.gametemplate.entities.WaypointEntity;
import com.germangascon.gametemplate.game.GameState;
import com.germangascon.gametemplate.math.Vector2;

import java.util.Optional;

/**
 * <p><strong>FinalTower</strong></p>
 * <p>Torre final (Base del jugador)</p>
 */
public class FinalTower extends Entity {
    private float cooldown;
    private float range;
    private final Timer timer;

    public FinalTower(float x, float y) {
        // Usamos la vida del GameState. Daño 5. Sprite torre_final.png
        super(x, y, 200, 200, 160, 160, GameState.getInstance().getLives(), 5, "/img/TorreFinal.png");
        this.range = 400; // Buen rango para defenderse
        this.cooldown = 1.0f;
        timer = new Timer();
    }

    @Override
    public void update(double deltaTime) {
        // Sincronizamos la vida visual de la torre con las vidas del juego
        this.hp = GameState.getInstance().getLives();

        timer.update(deltaTime);
        if (timer.every(cooldown)) {
            // Busca enemigos cercanos
            Optional<WaypointEntity> optionalTarget = gameContext.findNearestEntity(WaypointEntity.class, position, Entity::isAlive);
            if (optionalTarget.isPresent()) {
                WaypointEntity target = optionalTarget.get();
                float distance = target.getPosition().distance(position);
                if (distance <= range) {
                    Vector2 targetDirection = new Vector2(target.getDirection());
                    targetDirection.scl(target.getSpeed());
                    float t = interceptTime(position, target.getPosition(), targetDirection, Bullet.BULLET_SPEED);

                    Vector2 aim;
                    if (t <= 0f) {
                        aim = target.getPosition();
                    } else {
                        aim = new Vector2(target.getPosition()).mulAdd(targetDirection, t);
                    }
                    // Dispara (EntityFactory se encargará de usar bulletNieve.png al detectar que es FinalTower)
                    gameContext.getEntityFactory().spawnBullet(this, position.x, position.y, aim, 1);
                }
            }
        }
    }

    @Override
    public void onCollision(Entity entity) {
        // No hace nada especial al colisionar físicamente
    }

    // Utilidad para predecir disparo (copiado de Tower/SnowTower)
    private static float interceptTime(Vector2 shooterPos, Vector2 targetPos, Vector2 targetVel, float projectileSpeed) {
        float rx = targetPos.x - shooterPos.x;
        float ry = targetPos.y - shooterPos.y;

        float vx = targetVel.x;
        float vy = targetVel.y;

        float s2 = projectileSpeed * projectileSpeed;
        float v2 = vx * vx + vy * vy;

        float a = v2 - s2;
        float b = 2f * (rx * vx + ry * vy);
        float c = rx * rx + ry * ry;

        final float EPS = 1e-6f;

        if (Math.abs(a) < EPS) {
            if (Math.abs(b) < EPS) return 0f;
            float t = -c / b;
            return (t > 0f) ? t : 0f;
        }

        float disc = b * b - 4f * a * c;
        if (disc < 0f) return 0f;

        float sqrt = (float) Math.sqrt(disc);
        float t1 = (-b - sqrt) / (2f * a);
        float t2 = (-b + sqrt) / (2f * a);

        float t = Float.POSITIVE_INFINITY;
        if (t1 > 0f) t = t1;
        if (t2 > 0f && t2 < t) t = t2;

        return Float.isFinite(t) ? t : 0f;
    }
}