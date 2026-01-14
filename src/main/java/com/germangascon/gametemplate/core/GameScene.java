package com.germangascon.gametemplate.core;

import com.germangascon.gametemplate.entities.DynamicEntity;
import com.germangascon.gametemplate.entities.Entity;
import com.germangascon.gametemplate.game.EntityFactory;
import com.germangascon.gametemplate.math.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * <p><strong>GameScene</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-12<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public abstract class GameScene implements GameContext {
    private static final Font FONT = new Font("Monospaced", Font.PLAIN, Config.FPS_FONT_SIZE);
    private final List<Entity> entities;
    protected final AssetManager assetManager;
    protected final InputManager inputManager;
    protected Engine engine;
    private final List<Entity> toAdd;
    private final List<Entity> toRemove;
    private boolean gamePaused;
    private final Map<String, String> debugInfo;

    public GameScene() {
        entities = new ArrayList<>();
        toAdd = new ArrayList<>();
        toRemove = new ArrayList<>();
        assetManager = new AssetManager();
        inputManager = new InputManager();
        debugInfo = new HashMap<>();
        gamePaused = false;
    }

    /**
     * Reinicia la escena
     */
    protected void reset() {
        entities.clear();
        toAdd.clear();
        toRemove.clear();
    }

    /**
     * Pausa la actualización de físicas del juego pero el input y el render continuan ejecutándose
     */
    public void pause() {
        gamePaused = !gamePaused;
    }

    public void addDebugInfo(String key, String value) {
        debugInfo.put(key, value);
    }

    /**
     * Obtiene el gestor de input (teclado y ratón)
     * @return InputManager
     */
    @Override
    public InputManager getInputManager() {
        return inputManager;
    }

    /**
     * Añade al "mundo" la Entity recibida como parámetro
     * @param entity Entity a spawnear
     */
    public void spawn(Entity entity) {
        entity.setGameContext(this);
        toAdd.add(entity);
    }

    /**
     * Obtiene la fábrica de entities
     * @return EntityFactory
     */
    public abstract EntityFactory getEntityFactory();

    /**
     * Getter de sólo lectura para asegurarnos que nadie manipula la lista de Entities
     * @return Lista de Entities de sólo lectura
     */
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    /**
     * Obtiene un iterador con las Entities de tipo type que cumplen la condición condition
     * @param type Tipo de Entity
     * @param condition Condición
     * @return Iterador con las Entities
     * @param <T> Tipo de la Entity
     */
    @Override
    public <T extends Entity> Iterable<T> getEntitiesByCondition(Class<T> type, Predicate<T> condition) {
        return () -> entities.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(condition)
                .iterator();
    }

    /**
     * Busca la Entity de tipo type que cumpla la condición filter más cercana a location
     * @param type Tipo de la Entity
     * @param location Posición de referencia para determinar la más cercana
     * @param filter Condición de filtrado
     * @return Optional<Entity>
     * @param <T> Tipo de la Entity
     */
    @Override
    public <T extends Entity> Optional<T> findNearestEntity(Class<T> type, Vector2 location, Predicate<T> filter) {
        T best = null;
        float bestDist2 = Float.POSITIVE_INFINITY;

        for (Entity e : entities) {
            if (!type.isInstance(e)) continue;
            T t = type.cast(e);
            if (!filter.test(t)) continue;

            float d2 = t.getPosition().distance2(location);
            if (d2 < bestDist2) {
                bestDist2 = d2;
                best = t;
            }
        }
        return Optional.ofNullable(best);
    }


    @Override
    public int worldWidth() {
        return engine.getWidth();
    }

    @Override
    public int worldHeight() {
        return engine.getHeight();
    }

    /**
     * Elimina del "mundo" la Entity recibida como parámetro
     * @param entity Entity a eliminar
     */
    public void removeEntity(Entity entity) {
        toRemove.add(entity);
    }

    /**
     * Se llama una vez al inicio, después de crear el Engine y antes de empezar el bucle.
     * Aquí puedes crear entidades, cargar niveles, etc.
     */
    protected void init(Engine engine) {
        this.engine = engine;
    }

    /**
     * Carga todos los assets de la escena
     */
    protected abstract void loadAssets();

    /**
     * Actualiza la física del juego.
     * @param deltaTime tiempo en segundos desde el último update.
     */
    protected void update(double deltaTime) {
        if (!gamePaused) {
            for (Entity entity : entities) {
                entity.preUpdate(deltaTime);
                entity.update(deltaTime);
                entity.postUpdate(deltaTime);
            }
        }
    }

    /**
     * Comprueba si hay colisiones entre las distintas entities,
     * y en caso afirmativo, notifica a cada entity de la colisión
     */
    public void checkCollisions() {
        int n = entities.size();
        for (int i = 0; i < n; i++) {
            Entity a = entities.get(i);

            for (int j = i + 1; j < n; j++) {
                Entity b = entities.get(j);

                // 1) Primero: AABB (barato y evita hacer máscaras si no se tocan)
                if (!intersects(a, b)) {
                    continue;
                }

                // 2) Ahora: callbacks según "yo decido"
                boolean aWants = a.canCollideWith(b);
                boolean bWants = b.canCollideWith(a);

                if (aWants) {
                    a.onCollision(b);
                }
                if (bWants) {
                    b.onCollision(a);
                }
            }
        }
    }

    /**
     * Comprueba si las Entity a y b están colisionando
     * @param a Primera entity
     * @param b Segunda entity
     * @return true si hay colisión, false en caso contrario
     */
    private boolean intersects(Entity a, Entity b) {
        return a.getRight()   > b.getLeft()   &&
                a.getLeft()   < b.getRight()  &&
                a.getBottom() > b.getTop()    &&
                a.getTop()    < b.getBottom();
    }


    /**
     * Elimina las Entity que están "muertas" y las que están fuera de los límites del "mundo"
     */
    protected void cleanUp() {
        entities.forEach(entity -> {
            Vector2 position = entity.getPosition();
            if (position.x < -entity.getWidth() || position.x > engine.getWidth() + entity.getWidth()  ||
                    position.y < -entity.getHeight() || position.y > engine.getHeight() + entity.getHeight()) {
                entity.destroy();
            }
        });

        // Eliminamos las entities "muertas"
        entities.removeIf(e -> !e.isAlive());
    }

    /**
     * Actualiza la lógica del juego.
     * Este método está asegurado que se ejecutará después de haber
     * ejecutado todos los updates de todas las Entities
     * @param deltaTime tiempo en segundos desde el último update.
     */
    protected void lateUpdate(double deltaTime) {
        for (Entity entity : entities) {
            entity.lateUpdate(deltaTime);
        }

        entities.addAll(toAdd);
        entities.removeAll(toRemove);
        toAdd.clear();
        toRemove.clear();
    }

    /**
     * Procesa el input (ratón y teclado) de la escena
     */
    protected abstract void processInput();

    /**
     * Dibuja la scene utilizando el contexto gráfico recibido como parámetro
     * @param g Contexto gráfico para dibujar
     */
    protected abstract void draw(Graphics2D g);

    /**
     * Dibuja información de depuración
     * La cantidad de información que aparece depende de lo que se haya seleccionado en la configuración
     * @param g Contexto gráfico para dibujar
     */
    protected void debugDraw(Graphics2D g) {
        // Hitboxes
        g.setColor(Color.RED);
        if (Config.SHOW_HITBOXES) {
            entities.forEach(entity -> {
                g.drawRect((int) entity.getLeft(), (int) entity.getTop(), entity.getHitboxWidth(), entity.getHitboxHeight());
            });
        }

        // HUD
        if (Config.SHOW_DEBUG) {
            g.setColor(Color.WHITE);
            g.setFont(FONT);
            int fps = engine.getFps();
            int ups = engine.getUps();
            int digitos = Math.max((int) Math.log10(fps) + 1, (int) Math.log10(ups) + 1) + 3;
            g.drawString("fps: " + fps, engine.getWidth() - (digitos * Config.FPS_FONT_SIZE), 12);
            g.drawString("ups: " + ups, engine.getWidth() - (digitos * Config.FPS_FONT_SIZE), 24);
            g.drawString("ent: " + entities.size(), engine.getWidth() - (digitos * Config.FPS_FONT_SIZE), 36);
            int currentY = 12;
            for (Map.Entry<String, String> pair : debugInfo.entrySet()) {
                g.drawString(pair.getKey() + ": " + pair.getValue(), 10, currentY);
                currentY += Config.FPS_FONT_SIZE + 2;

            }
            // debugInfo.clear();
        }
    }

    /**
     * Dibuja la entity recibida como parámetro
     * @param g Contexto gráfico para dibujar
     * @param entity Entity a dibujar
     */
    protected void drawEntity(Graphics2D g, Entity entity) {
        Image sprite = assetManager.getSprite(entity.getSprite());
        if (sprite != null) {
            Vector2 drawPosition = entity.getDrawPosition();
            if (entity instanceof DynamicEntity dynamicEntity) {
                Vector2 direction = dynamicEntity.getDirection();
                float angle = (float) Math.atan2(direction.y, direction.x);
                AffineTransform old = g.getTransform();
                g.rotate(angle, drawPosition.x + entity.getWidth() / 2f, drawPosition.y + entity.getHeight() / 2f);
                g.drawImage(sprite, (int) drawPosition.x, (int) drawPosition.y, entity.getWidth(), entity.getHeight(), null);
                g.setTransform(old);
            } else {
                g.drawImage(sprite, (int) drawPosition.x, (int) drawPosition.y, entity.getWidth(), entity.getHeight(), null);
            }
        }
    }
}
