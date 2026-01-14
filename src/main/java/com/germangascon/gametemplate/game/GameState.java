package com.germangascon.gametemplate.game;

/**
 * <p><strong>GameState</strong></p>
 * <p>Singleton para gestionar el estado global del juego (Dinero, Vidas, Oleada)</p>
 */
public class GameState {
    private static GameState instance;

    private int money;
    private int lives;
    private int wave;
    private boolean gameOver;

    private GameState() {
        this.money = 250;
        this.lives = 30;
        this.wave = 1;
        this.gameOver = false;
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public void reset() {
        this.money = 100;
        this.lives = 20;
        this.wave = 1;
        this.gameOver = false;
    }

    public int getMoney() { return money; }
    public void addMoney(int amount) { this.money += amount; }
    public void setMoney(int money) { this.money = money; }
    public boolean spendMoney(int amount) {
        if (money >= amount) {
            money -= amount;
            return true;
        }
        return false;
    }

    public int getLives() { return lives; }
    public void setLives(int lives) { this.lives = lives; }
    public void decreaseLives(int amount) { this.lives -= amount; if (lives <= 0) gameOver = true; }
    public int getWave() { return wave; }
    public void setWave(int wave) { this.wave = wave; }
    public boolean isGameOver() { return gameOver; }
}