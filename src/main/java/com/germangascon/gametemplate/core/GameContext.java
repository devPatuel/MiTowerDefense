package com.germangascon.gametemplate.core;

import com.germangascon.gametemplate.entities.Entity;
import com.germangascon.gametemplate.game.EntityFactory;
import com.germangascon.gametemplate.math.Vector2;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p><strong>GameContext</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-15<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public interface GameContext {
    EntityFactory getEntityFactory();
    InputManager getInputManager();
    <T extends Entity> Iterable<T> getEntitiesByCondition(Class<T> type, Predicate<T> condition);
    <T extends Entity> Optional<T> findNearestEntity(Class<T> type, Vector2 location, Predicate<T> filter);
    int worldWidth();
    int worldHeight();
}
