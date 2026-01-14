package com.germangascon.gametemplate.game;

import com.germangascon.gametemplate.core.GameScene;
import com.germangascon.gametemplate.entities.Entity;
import com.germangascon.gametemplate.game.entities.*;
import com.germangascon.gametemplate.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p><strong>EntityFactory</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-15<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public class EntityFactory {
    private final GameScene gameScene;
    private final HashMap<Class<?>, List<Vector2>> waypointsMap;

    public EntityFactory(GameScene gameScene) {
        waypointsMap = new HashMap<>();
        generateWaypoints();
        this.gameScene = gameScene;
    }

    private void generateWaypoints() {
        List<Vector2> tankWaypoints = new ArrayList<>();
        tankWaypoints.add(new Vector2(192, 0));
        tankWaypoints.add(new Vector2(192, 30));
        tankWaypoints.add(new Vector2(192, 60));
        tankWaypoints.add(new Vector2(192, 90));
        tankWaypoints.add(new Vector2(192, 120));
        tankWaypoints.add(new Vector2(192, 150));
        tankWaypoints.add(new Vector2(192, 370));
        tankWaypoints.add(new Vector2(194, 378));
        tankWaypoints.add(new Vector2(197, 387));
        tankWaypoints.add(new Vector2(199, 392));
        tankWaypoints.add(new Vector2(205, 403));
        tankWaypoints.add(new Vector2(212, 412));
        tankWaypoints.add(new Vector2(230, 422));
        tankWaypoints.add(new Vector2(243, 429));
        tankWaypoints.add(new Vector2(577, 429));
        tankWaypoints.add(new Vector2(593, 423));
        tankWaypoints.add(new Vector2(602, 416));
        tankWaypoints.add(new Vector2(609, 406));
        tankWaypoints.add(new Vector2(612, 400));
        tankWaypoints.add(new Vector2(615, 392));
        tankWaypoints.add(new Vector2(617, 374));
        tankWaypoints.add(new Vector2(617, 287));
        tankWaypoints.add(new Vector2(621, 283));
        tankWaypoints.add(new Vector2(627, 272));
        tankWaypoints.add(new Vector2(637, 261));
        tankWaypoints.add(new Vector2(651, 253));
        tankWaypoints.add(new Vector2(672, 248));
        tankWaypoints.add(new Vector2(770, 248));
        waypointsMap.put(Tank.class, tankWaypoints);
        waypointsMap.put(Grinch.class, tankWaypoints);
        waypointsMap.put(Santa.class, tankWaypoints);
    }

    public Tank spawnTank(float x, float y, int level) {
        Tank tank = new Tank(x, y, 5, 1, 170, waypointsMap.get(Tank.class));
        gameScene.spawn(tank);
        return tank;
    }

    public Grinch spawnGrinch(float x, float y, int level) {
        Vector2 start = waypointsMap.get(Grinch.class).getFirst();
        Grinch grinch = new Grinch(start.x, start.y, 30, 1, 100, waypointsMap.get(Grinch.class));
        gameScene.spawn(grinch);
        return grinch;
    }

    public Santa spawnSanta(float x, float y, int level) {
        Vector2 start = waypointsMap.get(Santa.class).getFirst();
        Santa santa = new Santa(start.x, start.y, 80, 3, 80, waypointsMap.get(Santa.class));
        gameScene.spawn(santa);
        return santa;
    }

    public Tower spawnTower(float x, float y, int level) {
        Tower tower = new Tower(x, y, 10, 3, Tower.DEFAULT_RANGE, 0.8f);
        for (int i = 1; i < level; i++) {
            tower.upgrade();
        }
        gameScene.spawn(tower);
        return tower;
    }

    public SnowTower spawnSnowTower(float x, float y, int level) {
        SnowTower tower = new SnowTower(x, y, 10, 5, SnowTower.DEFAULT_RANGE, 1.0f);
        for (int i = 1; i < level; i++) {
            tower.upgrade();
        }
        gameScene.spawn(tower);
        return tower;
    }

    public FinalTower spawnFinalTower(float x, float y) {
        FinalTower tower = new FinalTower(x, y);
        gameScene.spawn(tower);
        return tower;
    }

    public Bullet spawnBullet(Entity owner, float x, float y, Vector2 target, int level) {
        int damage = (owner != null) ? owner.getDamage() : 1;
        String sprite = "/img/bullet.png";
        if (owner instanceof FinalTower) {
            sprite = "/img/bulletNieve.png";
        } else if (owner instanceof Tower) {
            int lvl = ((Tower) owner).getLevel();
            sprite = "/img/bullet.png";
        } else if (owner instanceof SnowTower) {
            int lvl = ((SnowTower) owner).getLevel();
            if (lvl == 1) sprite = "/img/bulletNieve.png";
            else if (lvl == 2) sprite = "/img/bulletNieve2.png";
            else if (lvl == 3) sprite = "/img/bulletNieve3.png";
        }
        Bullet bullet = new Bullet(owner, x, y, damage, target, sprite);
        gameScene.spawn(bullet);
        return bullet;
    }

    public AIBullet spawnAIBullet(Entity owner, float x, float y, Vector2 target, int level) {
        AIBullet aiBullet = new AIBullet(owner, x, y, 1, target);
        gameScene.spawn(aiBullet);
        return aiBullet;
    }

    public <T extends Entity> Spawner spawnSpawner(Class<T> entityClass, float x, float y, float cooldown, int level) {
        if (entityClass == Tank.class) {
            Spawner spawner = new Spawner(Tank.class, x, y, 1, 0, cooldown, level);
            gameScene.spawn(spawner);
            return spawner;
        } else if (entityClass == Grinch.class) {
            Spawner spawner = new Spawner(Grinch.class, x, y, 1, 0, cooldown, level);
            gameScene.spawn(spawner);
            return spawner;
        } else if (entityClass == Santa.class) {
            Spawner spawner = new Spawner(Santa.class, x, y, 1, 0, cooldown, level);
            gameScene.spawn(spawner);
            return spawner;
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
