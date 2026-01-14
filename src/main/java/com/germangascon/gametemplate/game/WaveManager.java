package com.germangascon.gametemplate.game;

import com.germangascon.gametemplate.core.GameContext;
import com.germangascon.gametemplate.entities.Entity;
import com.germangascon.gametemplate.game.entities.Grinch;
import com.germangascon.gametemplate.game.entities.Santa;

public class WaveManager {
    private final GameContext gameContext;
    private int currentWave = 0;
    private boolean waveInProgress = false;

    // Lógica de spawn
    private float spawnTimer = 0;
    private float spawnInterval = 1.5f;
    private int enemiesToSpawn = 0;
    private int enemiesSpawned = 0;
    private Class<? extends Entity> currentEnemyType;

    public WaveManager(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public void startNextWave() {
        if (waveInProgress) return;

        currentWave++;
        GameState.getInstance().setWave(currentWave);
        setupWave(currentWave);
        
        waveInProgress = true;
        enemiesSpawned = 0;
        spawnTimer = 0;
    }

    private void setupWave(int wave) {
        switch (wave) {
            case 1: enemiesToSpawn = 5;  currentEnemyType = Grinch.class; spawnInterval = 2.0f; break;
            case 2: enemiesToSpawn = 10;  currentEnemyType = Grinch.class; spawnInterval = 1.5f; break;
            case 3: enemiesToSpawn = 5;  currentEnemyType = Santa.class;  spawnInterval = 2.5f; break;
            case 4: enemiesToSpawn = 12; currentEnemyType = Santa.class;  spawnInterval = 1.8f; break;
            case 5: enemiesToSpawn = 40; currentEnemyType = Grinch.class; spawnInterval = 0.8f; break;
            case 6: enemiesToSpawn = 25; currentEnemyType = Santa.class; spawnInterval = 0.8f; break;
            case 7: enemiesToSpawn = 40; currentEnemyType = Santa.class; spawnInterval = 0.5f; break;
            default: enemiesToSpawn = 0; break; // Fin del juego
        }
    }

    public void update(double deltaTime) {
        if (!waveInProgress) return;

        // Fase de generación de enemigos
        if (enemiesSpawned < enemiesToSpawn) {
            spawnTimer += deltaTime;
            if (spawnTimer >= spawnInterval) {
                spawnTimer = 0;
                spawnEnemy();
                enemiesSpawned++;
            }
        } else {
            // Fase de espera (esperar a que mueran todos)
            if (isWaveCleared()) {
                waveInProgress = false;
            }
        }
    }

    private void spawnEnemy() {
        // Coordenadas de inicio del camino (basado en EntityFactory)
        float startX = 192;
        float startY = 370;

        if (currentEnemyType == Grinch.class) {
            gameContext.getEntityFactory().spawnGrinch(startX, startY, 1);
        } else if (currentEnemyType == Santa.class) {
            gameContext.getEntityFactory().spawnSanta(startX, startY, 1);
        }
    }

    private boolean isWaveCleared() {
        // Comprobamos si queda algún Grinch o Santa vivo
        boolean grinchAlive = gameContext.getEntitiesByCondition(Grinch.class, Entity::isAlive).iterator().hasNext();
        boolean santaAlive = gameContext.getEntitiesByCondition(Santa.class, Entity::isAlive).iterator().hasNext();
        return !grinchAlive && !santaAlive;
    }

    public boolean isWaveInProgress() {
        return waveInProgress;
    }
    
    public int getCurrentWave() { return currentWave; }

    public void setWave(int wave) {
        this.currentWave = wave;
        this.waveInProgress = false; // Al cargar, asumimos que estamos en fase de preparación
        this.enemiesSpawned = 0;
        this.spawnTimer = 0;
    }
}