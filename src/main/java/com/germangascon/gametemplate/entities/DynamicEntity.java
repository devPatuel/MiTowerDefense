package com.germangascon.gametemplate.entities;

import com.germangascon.gametemplate.math.Vector2;

/**
 * <p><strong>DynamicEntity</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-12<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public abstract class DynamicEntity extends Entity {
    protected Vector2 direction;
    protected float speed;

    public DynamicEntity(float x, float y, int width, int height, int hitboxWidth, int hitboxHeight, int hp, int damage, String sprite, Vector2 target, float speed) {
        super(x, y, width, height, hitboxWidth, hitboxHeight, hp, damage, sprite);
        this.speed = speed;
        this.direction = new Vector2();
        setDireccion(target);
    }

    public float getSpeed() {
        return speed;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public void setDireccion(Vector2 target) {
        if (target == null) {
            direction.set(0f, 0f);
            return;
        }
        direction.set(target).sub(position).nor();
    }

    @Override
    public void update(double deltaTime) {
        position.mulAdd(direction, speed * (float) deltaTime);
    }
}
