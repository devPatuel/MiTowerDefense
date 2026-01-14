package com.germangascon.gametemplate.entities;

import com.germangascon.gametemplate.math.Vector2;

/**
 * <p><strong>AIDynamicEntity2</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-14<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public abstract class AIDynamicEntity extends DynamicEntity {
    private Vector2 target;

    public AIDynamicEntity(float x, float y, int width, int height, int hitboxWidth, int hitboxHeight, int hp, int damage, String sprite, Vector2 target, float speed) {
        super(x, y, width, height, hitboxWidth, hitboxHeight, hp, damage, sprite, target, speed);
        this.target = target;
    }

    public Vector2 getTarget() {
        return target;
    }

    public void setTarget(Vector2 target) {
        this.target = target;
    }

    @Override
    public void update(double deltaTime) {
        float step = speed * (float) deltaTime;
        float dist = position.distance(target);

        // Si llegamos en este frame, clavamos posición
        if (step >= dist) {
            position.set(target);
            return;
        }

        // Establecemos la dirección normalizada hacia el target (perseguimos al target)
        setDireccion(target);

        // Reutiliza update() de DynamicEntity: position += velocity * dt + direction por velocity
        super.update(deltaTime);
    }

}
