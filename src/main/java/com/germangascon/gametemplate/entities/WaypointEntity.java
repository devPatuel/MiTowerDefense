package com.germangascon.gametemplate.entities;

import com.germangascon.gametemplate.game.GameState;
import com.germangascon.gametemplate.math.Vector2;

import java.util.List;

/**
 * <p><strong>Waypoint2Entity</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-12<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public abstract class WaypointEntity extends DynamicEntity {
    private final List<Vector2> waypoints;
    private int currentWaypoint;

    public WaypointEntity(float x, float y, int width, int height, int hitboxWidth, int hitboxHeight, int hp, int damage, String sprite,
                          float speed, List<Vector2> waypoints) {
        super(x, y, width, height, hitboxWidth, hitboxHeight, hp, damage, sprite, waypoints.getFirst(), speed);
        this.waypoints = waypoints;
        this.currentWaypoint = 0;
    }

    @Override
    public void update(double deltaTime) {
        // Sin waypoints o ya hemos terminado
        if (waypoints == null || waypoints.isEmpty() || currentWaypoint >= waypoints.size()) {
            return;
        }

        Vector2 target = waypoints.get(currentWaypoint);

        // Distancia desde el centro actual al waypoint
        float dist = position.distance(target);

        // Paso máximo que podemos dar este frame
        float step = speed * (float) deltaTime;

        if (step >= dist) {
            // Llegamos (o nos pasamos) a este waypoint en este frame:
            // 1) clavamos posición en el waypoint
            // 2) avanzamos al siguiente
            position.set(target);

            // Avanzamos siempre el índice
            currentWaypoint++;
            
            // Si hay siguiente waypoint, recalculamos la velocidad
            if (currentWaypoint < waypoints.size()) {
                Vector2 nextTarget = waypoints.get(currentWaypoint);
                setDireccion(nextTarget);
            } else {
                // Hemos llegado al último waypoint
                onPathFinished();
            }
        } else {
            // Movimiento normal del frame con la velocidad actual
            super.update(deltaTime);
        }
    }

    protected void onPathFinished() {
        GameState.getInstance().decreaseLives(getDamage());
        destroy();
    }

    @Override
    public void reset() {
        // Restaurar posición, hp, etc. usando la lógica de Entity
        super.reset();
        currentWaypoint = 0;

        // Recalcular velocidad hacia el primer waypoint (si existe)
        if (waypoints != null && !waypoints.isEmpty()) {
            Vector2 first = waypoints.getFirst();
            setDireccion(first);
        }
    }

    public void goToNearestWaypoint() {
        float minDistance = Float.MAX_VALUE;
        int nearestWaypoint = 0;
        for (int i = 0; i < waypoints.size(); i++) {
            Vector2 waypoint = waypoints.get(i);
            float distance2 = position.distance(waypoint);
            if (distance2 < minDistance ) {
                minDistance = distance2;
                nearestWaypoint = i;
            }
        }
        currentWaypoint = nearestWaypoint;
        // Calculamos la nueva velocity (dirección) para alcanzar el waypoint
        Vector2 nextTarget = waypoints.get(currentWaypoint);
        setDireccion(nextTarget);
    }

    public void goToNearestWaypointNoBacktrack() {
        int start = Math.max(0, currentWaypoint);
        int end = Math.min(waypoints.size() - 1, start);

        float bestDist2 = Float.MAX_VALUE;
        int bestIdx = start;

        for (int i = start; i <= end; i++) {
            Vector2 wp = waypoints.get(i);
            float d2 = position.distance2(wp); // usa dst2 si existe
            if (d2 < bestDist2) {
                bestDist2 = d2;
                bestIdx = i;
            }
        }

        currentWaypoint = bestIdx;

        Vector2 target = waypoints.get(currentWaypoint);
        setDireccion(target);
    }
}
