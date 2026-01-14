package com.germangascon.gametemplate.game.entities;

import com.germangascon.gametemplate.core.Timer;
import com.germangascon.gametemplate.entities.Entity;
import com.germangascon.gametemplate.entities.WaypointEntity;
import com.germangascon.gametemplate.math.Vector2;

import java.util.Optional;

/**
 * <p><strong>Tower</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-13<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public class Tower extends Entity {
    public static final int COST = 50;
    public static final int UPGRADE_COST = 40;
    public static final float DEFAULT_RANGE = 200f;
    private int level = 1;
    private float cooldown;
    private float range;
    private final Timer timer;

    public Tower(float x, float y, int hp, int damage, float range, float cooldown) {
        super(x, y, 60, 100, 45, 82, hp, damage, "/img/tower.png");
        this.range = range;
        this.cooldown = cooldown;
        timer = new Timer();
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void upgrade() {
        if (level < 3) {
            level++;
            this.damage += 2; // Aumenta daño
            this.range += 20; // Aumenta rango
            this.setSprite("/img/tower" + level + ".png");
            System.out.println("Torre mejorada a nivel " + level);
        }
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void onCollision(Entity entity) {

    }

    @Override
    public void update(double deltaTime) {
        timer.update(deltaTime);
        if (timer.every(cooldown)) {
            // Buscamos cualquier enemigo (WaypointEntity) en lugar de solo Tank
            Optional<WaypointEntity> optionalTarget = gameContext.findNearestEntity(WaypointEntity.class, position, Entity::isAlive);
            if (optionalTarget.isPresent()) {
                WaypointEntity target = optionalTarget.get();
                float distance = target.getPosition().distance(position);
                if (distance <= range) {
                    Vector2 targetDirection = new Vector2(target.getDirection());
                    targetDirection.scl(target.getSpeed());
                    float t = interceptTime(position, target.getPosition(), targetDirection, Bullet.BULLET_SPEED);

                    // Si no hay solución (bala demasiado lenta o raíces negativas), dispara “a lo tonto”
                    Vector2 aim;
                    if (t <= 0f) {
                        aim = target.getPosition();
                    } else {
                        aim = new Vector2(target.getPosition()).mulAdd(targetDirection, t);
                    }
                    gameContext.getEntityFactory().spawnBullet(this, position.x, position.y, aim, 1);
                }
            }
        }
    }

    // Calcula el tiempo que tardará en interceptar el objetivo
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

        // Caso casi lineal
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
