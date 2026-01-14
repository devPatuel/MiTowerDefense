package com.germangascon.gametemplate.game.scenes;

import com.germangascon.gametemplate.core.Config;
import com.germangascon.gametemplate.core.GameScene;
import com.germangascon.gametemplate.db.GameRepository;
import com.germangascon.gametemplate.entities.Entity;
import com.germangascon.gametemplate.game.EntityFactory;
import com.germangascon.gametemplate.game.WaveManager;
import com.germangascon.gametemplate.entities.WaypointEntity;
import com.germangascon.gametemplate.game.GameState;
import com.germangascon.gametemplate.game.entities.Tower;
import com.germangascon.gametemplate.game.entities.SnowTower;
import com.germangascon.gametemplate.game.entities.FinalTower;
import com.germangascon.gametemplate.math.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p><strong>Game</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-12<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public class ExampleScene extends GameScene {
    private final EntityFactory entityFactory;
    private final WaveManager waveManager;
    private final GameRepository gameRepository;
    private enum State { MENU, PLAYING }
    private State currentState = State.MENU;
    private String selectedTowerToPlace = null;
    
    // Lista de puntos donde se permite construir
    private final List<Vector2> buildSites;

    public ExampleScene() {
        this.entityFactory = new EntityFactory(this);
        this.waveManager = new WaveManager(this);
        this.gameRepository = new GameRepository(this);
        
        // Definimos las posiciones fijas para las torres
        // Estas coordenadas están pensadas para no chocar con el camino definido en EntityFactory
        this.buildSites = new ArrayList<>();
        buildSites.add(new Vector2(239, 209));
        buildSites.add(new Vector2(147, 352));
        buildSites.add(new Vector2(168, 451));
        buildSites.add(new Vector2(300, 300));
        buildSites.add(new Vector2(380, 380));
        buildSites.add(new Vector2(298, 493));
        buildSites.add(new Vector2(440, 493));
        buildSites.add(new Vector2(555, 325));
        buildSites.add(new Vector2(664, 332));
        buildSites.add(new Vector2(655, 200));
        buildSites.add(new Vector2(640, 487));
        buildSites.add(new Vector2(479, 296));

        spawnEntities();
    }

    @Override
    protected void reset() {
        super.reset();
        GameState.getInstance().reset();
        spawnEntities();
    }

    @Override
    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    private void spawnEntities() {
        Vector2 firstSpot = buildSites.get(2); // Ponemos una en el centro
        entityFactory.spawnTower(firstSpot.x, firstSpot.y, 1);

        // Torre Final (Base) al final del camino
        entityFactory.spawnFinalTower(770, 215);
    }

    @Override
    public void loadAssets() {
        try {
            assetManager.loadSprite("/img/background2.png");
            assetManager.loadSprite("/img/background_Inicio.jpg");
            assetManager.loadSprite("/img/tank.png");
            assetManager.loadSprite("/img/bullet.png");
            assetManager.loadSprite("/img/bulletNieve.png");
            assetManager.loadSprite("/img/bulletNieve2.png");
            assetManager.loadSprite("/img/bulletNieve3.png");
             assetManager.loadSprite("/img/tower.png");
            assetManager.loadSprite("/img/tower2.png");
            assetManager.loadSprite("/img/tower3.png");
            assetManager.loadSprite("/img/TorreFinal.png");
            assetManager.loadSprite("/img/torre_nieve.png");
            assetManager.loadSprite("/img/torre_nieve2.png");
            assetManager.loadSprite("/img/torre_nieve3.png");
            assetManager.loadSprite("/img/grinch.png");
            assetManager.loadSprite("/img/santa.png");
            assetManager.loadSprite("/img/coin.png");
            assetManager.loadSprite("/img/heart.png");
            assetManager.loadSprite("/img/flag.png");
        } catch (IOException e) {
            throw new RuntimeException("Error cargando assets", e);
        }
    }

    @Override
    public void update(double deltaTime) {
        if (currentState == State.PLAYING) {
            super.update(deltaTime);
            waveManager.update(deltaTime);
        }
    }

    @Override
    public void processInput() {
        if (currentState == State.MENU) {
            int mouseX = inputManager.getMouseX();
            int mouseY = inputManager.getMouseY();

            if (inputManager.hasLeftClick()) {
                int w = engine.getWidth();
                int h = engine.getHeight();
                int btnW = 220;
                int btnH = 60;
                int btnX = (w - btnW) / 2;
                int btnY1 = h / 2;
                int btnY2 = h / 2 + 80;

                // Nueva Partida
                if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY1 && mouseY <= btnY1 + btnH) {
                    reset();
                    currentState = State.PLAYING;
                }

                // Cargar Partida
                if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY2 && mouseY <= btnY2 + btnH) {
                    reset();
                    gameRepository.loadGame();
                    waveManager.setWave(GameState.getInstance().getWave());
                    currentState = State.PLAYING;
                }
            }
            return;
        }

        List<Entity> entities = getEntities();
        int mouseX = inputManager.getMouseX();
        int mouseY = inputManager.getMouseY();

        if (Config.SHOW_DEBUG) {
            addDebugInfo("Mouse", "(" + mouseX + ", " + mouseY + ")");
        }

        if (inputManager.hasLeftClick()) {
            int w = engine.getWidth();

            // Botón "Guardar y Salir" (Solo si NO hay oleada en curso)
            if (!waveManager.isWaveInProgress()) {
                int btnW = 180;
                int btnH = 50;
                int btnX = w - btnW - 20;
                int btnY = 20;

                if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
                    gameRepository.saveGame();
                    currentState = State.MENU;
                    return;
                }

                // Botones de compra (Selección)
                int panelHeight = 50;
                int panelY = 10;
                int gap = 10;
                int buyPanelWidth = 120;
                int startX = 20;
                int startY = panelY + panelHeight + 10;

                // Botón Torre Básica
                if (mouseX >= startX && mouseX <= startX + buyPanelWidth && mouseY >= startY && mouseY <= startY + panelHeight) {
                    selectedTowerToPlace = "BASIC";
                    return;
                }

                // Botón Torre Nieve
                int snowY = startY + panelHeight + gap;
                if (mouseX >= startX && mouseX <= startX + buyPanelWidth && mouseY >= snowY && mouseY <= snowY + panelHeight) {
                    selectedTowerToPlace = "SNOW";
                    return;
                }

                // Mejorar Torre (Si no estamos seleccionando una para poner)
                if (selectedTowerToPlace == null) {
                    for (Entity e : getEntities()) {
                        if (e instanceof Tower || e instanceof SnowTower) {
                            // Comprobar si el clic está dentro de la torre
                            if (mouseX >= e.getLeft() && mouseX <= e.getRight() &&
                                mouseY >= e.getTop() && mouseY <= e.getBottom()) {

                                int upgradeCost = 0;
                                boolean canUpgrade = false;

                                if (e instanceof Tower) {
                                    Tower t = (Tower) e;
                                    if (t.getLevel() < 3) {
                                        upgradeCost = Tower.UPGRADE_COST;
                                        canUpgrade = true;
                                    }
                                } else if (e instanceof SnowTower) {
                                    SnowTower t = (SnowTower) e;
                                    if (t.getLevel() < 3) {
                                        upgradeCost = SnowTower.UPGRADE_COST;
                                        canUpgrade = true;
                                    }
                                }

                                if (canUpgrade && GameState.getInstance().spendMoney(upgradeCost)) {
                                    if (e instanceof Tower) ((Tower) e).upgrade();
                                    else ((SnowTower) e).upgrade();
                                }
                                return; // Clic consumido
                            }
                        }
                    }
                }
            }

            // Colocar Torre (Lógica de Slots)
            if (!waveManager.isWaveInProgress() && selectedTowerToPlace != null) {
                int cost = 0;
                if ("BASIC".equals(selectedTowerToPlace)) {
                    cost = Tower.COST;
                } else if ("SNOW".equals(selectedTowerToPlace)) {
                    cost = SnowTower.COST;
                }

                if (GameState.getInstance().getMoney() >= cost) {
                    // Buscamos si hemos hecho clic cerca de un slot válido
                    Vector2 targetSlot = getClickedBuildSpot(mouseX, mouseY);
                    
                    if (targetSlot != null && !isSpotOccupied(targetSlot)) {
                        GameState.getInstance().spendMoney(cost);
                        if ("BASIC".equals(selectedTowerToPlace)) {
                            entityFactory.spawnTower(targetSlot.x, targetSlot.y, 1);
                        } else {
                            entityFactory.spawnSnowTower(targetSlot.x, targetSlot.y, 1);
                        }
                        selectedTowerToPlace = null; // Deseleccionar tras colocar
                    }
                }
            }
        }

        if (inputManager.hasMiddleClick()) {
            if (Config.SHOW_DEBUG) {
                addDebugInfo("MiddleClick", "(" + mouseX + ", " + mouseY + ")");
            }
        }

        if (inputManager.hasRightClick()) {
            selectedTowerToPlace = null;
        }

        if (inputManager.isKeyJustPressed(KeyEvent.VK_G)) {
            Config.SHOW_GRID = !Config.SHOW_GRID;
        }

        if (inputManager.isKeyJustPressed(KeyEvent.VK_F3)) {
            Config.SHOW_DEBUG = !Config.SHOW_DEBUG;
        }

        if (inputManager.isKeyJustPressed(KeyEvent.VK_F4)) {
            Config.SHOW_HITBOXES = !Config.SHOW_HITBOXES;
        }

        if (inputManager.isKeyJustPressed(KeyEvent.VK_R)) {
            reset();
        }

        if (inputManager.isKeyJustPressed(KeyEvent.VK_P)) {
            pause();
        }

        if (inputManager.isKeyJustPressed(KeyEvent.VK_ENTER)) {
            if (!waveManager.isWaveInProgress() && !GameState.getInstance().isGameOver()) {
                waveManager.startNextWave();
            }
        }
        
    }

    /**
     * Devuelve el slot de construcción si el ratón está cerca (radio de tolerancia)
     */
    private Vector2 getClickedBuildSpot(int mouseX, int mouseY) {
        float tolerance = 40f; // Radio de clic
        Vector2 mousePos = new Vector2(mouseX, mouseY);
        
        for (Vector2 spot : buildSites) {
            if (spot.distance(mousePos) <= tolerance) {
                return spot;
            }
        }
        return null;
    }

    /**
     * Comprueba si ya hay una torre en ese slot
     */
    private boolean isSpotOccupied(Vector2 spot) {
        for (Entity e : getEntities()) {
            if (e instanceof Tower || e instanceof SnowTower) {
                // Si la distancia es muy pequeña (casi 0), es que está en ese slot
                if (e.getPosition().distance(spot) < 5f) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g) {
        // Fondo
        Image bg = assetManager.getSprite("/img/background2.png");
        if (bg != null) {
            g.drawImage(bg, 0, 0, engine.getWidth(), engine.getHeight(), null);
        }

        if (currentState == State.MENU) {
            drawMenu(g);
            return;
        }

        // Dibujar Slots de construcción (Solo si estamos en fase de preparación)
        if (!waveManager.isWaveInProgress()) {
            for (Vector2 spot : buildSites) {
                // Si está ocupado, no dibujamos el indicador de "disponible"
                if (!isSpotOccupied(spot)) {
                    g.setColor(new Color(255, 255, 255, 100)); // Blanco semitransparente
                    g.fillOval((int)spot.x - 10, (int)spot.y - 10, 20, 20);
                    g.setColor(new Color(0, 0, 0, 50));
                    g.drawOval((int)spot.x - 10, (int)spot.y - 10, 20, 20);
                }
            }
        }

        // Entidades
        for (Entity entity : getEntities()) {
            drawEntity(g, entity);

            // Barra de vida para enemigos
            if (entity instanceof WaypointEntity && entity.isAlive()) {
                int w = entity.getWidth();
                int h = 6;
                int x = (int)entity.getX() - w / 2;
                int y = (int)entity.getY() - entity.getHeight() / 2 - 10;

                float hpPct = (float)entity.getHp() / entity.getMaxHp();

                g.setColor(Color.RED);
                g.fillRect(x, y, w, h);
                g.setColor(Color.GREEN);
                g.fillRect(x, y, (int)(w * hpPct), h);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, w, h);
            }
        }

        // Interfaz de Usuario (UI)
        drawUI(g);

        if (GameState.getInstance().isGameOver()) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", engine.getWidth() / 2 - 150, engine.getHeight() / 2);
        } else if (!waveManager.isWaveInProgress()) {
            // Mensaje para iniciar siguiente oleada
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String msg = "Pulsa ENTER para iniciar Oleada " + (GameState.getInstance().getWave() + (waveManager.getCurrentWave() == GameState.getInstance().getWave() ? 1 : 0));
            
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(msg);
            int x = (engine.getWidth() - textWidth) / 2;
            int y = engine.getHeight() - 100;

            g.setColor(Color.BLACK);
            g.fillRect(x - 10, y - 20, textWidth + 20, 30);
            g.setColor(Color.YELLOW);
            g.drawString(msg, x, y);
        }

        // Visualización del Rango de la torre seleccionada
        if (selectedTowerToPlace != null && !waveManager.isWaveInProgress()) {
            int mx = inputManager.getMouseX();
            int my = inputManager.getMouseY();
            
            // Si el ratón está sobre un slot válido, dibujamos el rango desde el slot, no desde el ratón
            Vector2 snapSlot = getClickedBuildSpot(mx, my);
            float drawX = mx;
            float drawY = my;
            
            if (snapSlot != null && !isSpotOccupied(snapSlot)) {
                drawX = snapSlot.x;
                drawY = snapSlot.y;
                // Dibujamos un indicador de que se va a colocar ahí
                g.setColor(Color.GREEN);
                g.drawOval((int)drawX - 20, (int)drawY - 20, 40, 40);
            }

            float r = 0;
            if ("BASIC".equals(selectedTowerToPlace)) r = Tower.DEFAULT_RANGE;
            else if ("SNOW".equals(selectedTowerToPlace)) r = SnowTower.DEFAULT_RANGE;

            if (r > 0) {
                g.setColor(new Color(255, 255, 255, 50)); // Blanco transparente
                g.fillOval((int)(drawX - r), (int)(drawY - r), (int)(r * 2), (int)(r * 2));
                g.setColor(Color.WHITE);
                g.drawOval((int)(drawX - r), (int)(drawY - r), (int)(r * 2), (int)(r * 2));
            }
        }
    }

    private void drawUI(Graphics2D g) {
        int width = engine.getWidth();
        int height = engine.getHeight();
        // Configuración del panel
        int panelWidth = 360;
        int panelHeight = 50;
        int panelX = (width - panelWidth) / 2; // Centrado horizontalmente
        int panelY = 10; // Un poco separado del borde superior

        // Fondo para Debug Info (FPS, Coordenadas) - Arriba a la izquierda
        if (Config.SHOW_DEBUG) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(2, 2, 180, 60);
        }

        // Fondo sutil (Negro semitransparente)
        g.setColor(new Color(0, 0, 0, 60));
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        // Configuración de fuente y color
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Posiciones relativas dentro del panel
        int iconSize = 32;
        int textOffsetY = 32; // Ajuste vertical para el texto
        int iconOffsetY = 9; // Ajuste vertical para el icono

        // 1. Oleada (Izquierda)
        Image flagImg = assetManager.getSprite("/img/flag.png");
        if (flagImg != null) g.drawImage(flagImg, panelX + 20, panelY + iconOffsetY, iconSize, iconSize, null);
        g.drawString("Oleada " + GameState.getInstance().getWave(), panelX + 60, panelY + textOffsetY);

        // 2. Vidas (Centro)
        Image heartImg = assetManager.getSprite("/img/heart.png");
        if (heartImg != null) g.drawImage(heartImg, panelX + 140, panelY + iconOffsetY, iconSize, iconSize, null);
        g.drawString("" + GameState.getInstance().getLives(), panelX + 180, panelY + textOffsetY);

        // 3. Dinero (Derecha)
        Image coinImg = assetManager.getSprite("/img/coin.png");
        if (coinImg != null) g.drawImage(coinImg, panelX + 240, panelY + iconOffsetY, iconSize, iconSize, null);
        g.drawString("" + GameState.getInstance().getMoney(), panelX + 280, panelY + textOffsetY);

        // Instrucciones de compra (Solo visibles si se puede comprar)
        if (!waveManager.isWaveInProgress()) {
            int gap = 10;
            int buyPanelWidth = 120;
            // Calculamos la posición para que estén centrados debajo del panel principal
            int startX = 20;
            int startY = panelY + panelHeight + 10;

            // Panel Izquierdo (Torre Básica)
            drawBuyPanel(g, startX, startY, buyPanelWidth, panelHeight, "/img/tower.png", Tower.COST, "Básica", "BASIC".equals(selectedTowerToPlace));

            // Panel Derecho (Torre Nieve) - Ahora debajo
            drawBuyPanel(g, startX, startY + panelHeight + gap, buyPanelWidth, panelHeight, "/img/torre_nieve.png", SnowTower.COST, "Nieve", "SNOW".equals(selectedTowerToPlace));
            
            // Botón "Guardar y Salir" (Arriba a la derecha)
            int btnW = 180;
            int btnH = 50;
            int btnX = width - btnW - 20;
            int btnY = 20;

            g.setColor(new Color(0, 0, 0, 180));
            g.fillRoundRect(btnX, btnY, btnW, btnH, 15, 15);
            g.setColor(Color.WHITE);
            g.drawRoundRect(btnX, btnY, btnW, btnH, 15, 15);
            
            String btnText = "Guardar y Salir";
            g.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(btnText, btnX + (btnW - fm.stringWidth(btnText)) / 2, btnY + (btnH + fm.getAscent()) / 2 - 2);
        }
    }

    private void drawBuyPanel(Graphics2D g, int x, int y, int width, int height, String iconPath, int cost, String name, boolean selected) {
        // Fondo negro
        if (selected) {
            g.setColor(new Color(255, 215, 0, 200)); // Dorado si está seleccionado
        } else {
            g.setColor(new Color(0, 0, 0, 200));
        }
        g.fillRoundRect(x, y, width, height, 20, 20);

        // Icono
        Image icon = assetManager.getSprite(iconPath);
        if (icon != null) {
            g.drawImage(icon, x + 10, y + 9, 32, 32, null);
        }

        // Texto
        if (selected) g.setColor(Color.BLACK);
        else g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(cost + " $", x + 50, y + 20);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString(name, x + 50, y + 38);
    }

    private void drawMenu(Graphics2D g) {
        int w = engine.getWidth();
        int h = engine.getHeight();

        // Fondo del menú
        Image bg = assetManager.getSprite("/img/background_Inicio.jpg");
        if (bg != null) {
            g.drawImage(bg, 0, 0, w, h, null);
        }

        // Título
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        String title = "TOWER DEFENSE";
        FontMetrics fm = g.getFontMetrics();
        int titleW = fm.stringWidth(title);
        g.drawString(title, (w - titleW) / 2, h / 3);

        // Botones
        int btnW = 220;
        int btnH = 60;
        int btnX = (w - btnW) / 2;
        int btnY1 = h / 2;
        int btnY2 = h / 2 + 80;

        // Botón 1: Nueva Partida
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(btnX, btnY1, btnW, btnH, 20, 20);
        g.setColor(Color.WHITE);
        g.drawRoundRect(btnX, btnY1, btnW, btnH, 20, 20);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String txt1 = "Nueva Partida";
        fm = g.getFontMetrics();
        g.drawString(txt1, btnX + (btnW - fm.stringWidth(txt1)) / 2, btnY1 + (btnH + fm.getAscent()) / 2 - 5);

        // Botón 2: Cargar Partida
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(btnX, btnY2, btnW, btnH, 20, 20);
        g.setColor(Color.WHITE);
        g.drawRoundRect(btnX, btnY2, btnW, btnH, 20, 20);
        String txt2 = "Cargar Partida";
        g.drawString(txt2, btnX + (btnW - fm.stringWidth(txt2)) / 2, btnY2 + (btnH + fm.getAscent()) / 2 - 5);
    }
}
