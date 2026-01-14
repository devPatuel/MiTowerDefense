package com.germangascon.gametemplate.core;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

/**
 * <p><strong>Game</strong></p>
 * Motor clásico con Canvas + BufferStrategy.
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-10<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.2
 * @since 0.0.1
 **/
public class Engine extends Canvas implements Runnable {

    private final int width;
    private final int height;
    private final int tileSize;
    private final int xTiles;
    private final int yTiles;
    private final int targetFps;
    private final int targetUpdates;

    private Thread gameThread;
    private volatile boolean running;

    // Contadores internos (no visibles)
    private int currentFps;
    private int currentUpdates;

    // Valores visibles para HUD (leídos desde el hilo de render)
    private volatile int fps;
    private volatile int ups;

    // Procesar teclado y ratón
    private InputManager inputManager;

    // BufferStrategy para render clásico
    private BufferStrategy bufferStrategy;

    private GameScene currentScene;

    public Engine(GameScene defaultScene, int width, int height, int tileSize, int targetFps, int targetUpdates) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.xTiles = width / tileSize;
        this.yTiles = height / tileSize;
        this.targetFps = targetFps;
        this.targetUpdates = targetUpdates;
        setScene(defaultScene);

        currentScene.init(this);

        this.currentFps = this.fps = 0;
        this.currentUpdates = this.ups = 0;

        setPreferredSize(new Dimension(width, height));
        // En Canvas no queremos el repintado automático de Swing
        setIgnoreRepaint(true);
        setFocusable(true);

        addKeyListener(inputManager);
        addMouseListener(inputManager);
        addMouseMotionListener(inputManager);

        currentScene.loadAssets();
    }

    public Engine(GameScene defaultScene, int width, int height, int tileSize) {
        this(defaultScene, width, height, tileSize, 60, 60);
    }

    /**
     * Método que permite cambiar la escena actual
     * @param scene Nueva escena
     */
    public void setScene(GameScene scene) {
        this.currentScene = scene;
        inputManager = currentScene.getInputManager();
        currentScene.loadAssets();
    }

    public int getFps() {
        return fps;
    }

    public int getUps() {
        return ups;
    }

    /**
     * Se llama cuando el Canvas se añade a un contenedor.
     * Es un buen momento para crear la BufferStrategy y arrancar el hilo del juego.
     */
    @Override
    public void addNotify() {
        super.addNotify();

        if (gameThread == null || !running) {
            // Creamos el buffer de dibujo (doble buffer)
            createBufferStrategy(2);
            bufferStrategy = getBufferStrategy();

            running = true;
            gameThread = new Thread(this, "GameThread");
            gameThread.start();
        }
    }

    /**
     * Actualiza la lógica del juego.
     * @param deltaTime Cantidad de tiempo, medida en segundos, que ha transcurrido desde el último frame.
     */
    public void update(double deltaTime) {
        currentUpdates++;
        currentScene.update(deltaTime);
        currentScene.checkCollisions();
        currentScene.lateUpdate(deltaTime);
        // Limpieza: eliminar entities muertas y fuera del mundo
        currentScene.cleanUp();
    }

    /**
     * Dibuja el frame actual sobre el Graphics2D proporcionado.
     * Aquí no se gestiona el BufferStrategy, solo el contenido.
     * @param g El contexto gráfico para poder dibujar en el canvas
     */
    public void draw(Graphics2D g) {
        // Limpiamos fondo
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        currentScene.draw(g);

        // Grid opcional
        if (Config.SHOW_GRID) {
            showGrid(g);
        }

        // Pintamos información de depuración
        currentScene.debugDraw(g);
    }

    private void showGrid(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < xTiles; x++) {
            int px = x * tileSize;
            g.drawLine(px, 0, px, height);
        }
        for (int y = 0; y < yTiles; y++) {
            int py = y * tileSize;
            g.drawLine(0, py, width, py);
        }
    }

    /**
     * Procesa el input recogido por el EDT y lo aplica
     * al mundo del juego (hilo del juego).
     */
    public void processInput() {
        currentScene.processInput();
        if (inputManager.hasLeftClick()) {
            inputManager.consumeLeftClick();
        }
        if (inputManager.hasRightClick()) {
            inputManager.consumeRightClick();
        }
        if (inputManager.hasMiddleClick()) {
            inputManager.consumeMiddleClick();
        }
        inputManager.reset();
    }

    /**
     * Bucle principal del juego: update + render en el mismo hilo.
     */
    @Override
    public void run() {
        final long NANOS_IN_SECOND = 1_000_000_000L;
        final long UPDATE_STEP_NANOS = (long)(NANOS_IN_SECOND / (double) targetUpdates);
        final long FRAME_STEP_NANOS  = (long)(NANOS_IN_SECOND / (double) targetFps);

        long lastTime = System.nanoTime();
        long accumulator = 0L;
        long frameAccumulator = 0L;

        long statsTimer = System.nanoTime();
        currentFps = 0;
        currentUpdates = 0;

        while (running) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;
            lastTime = now;

            accumulator += elapsed;
            frameAccumulator += elapsed;

            boolean processed = false;

            // cap para evitar spiral of death
            if (accumulator > 250_000_000L) accumulator = 250_000_000L;

            // input una vez por iteración (opcional)
            processInput();

            while (accumulator >= UPDATE_STEP_NANOS) {
                update(UPDATE_STEP_NANOS / (double)NANOS_IN_SECOND);
                accumulator -= UPDATE_STEP_NANOS;
                processed = true;
            }

            if (frameAccumulator >= FRAME_STEP_NANOS) {
                render();
                frameAccumulator %= FRAME_STEP_NANOS; // evita backlog
                processed = true;
            }

            long statsNow = System.nanoTime();
            long statsElapsed = statsNow - statsTimer;
            if (statsElapsed >= NANOS_IN_SECOND) {
                double seconds = statsElapsed / (double) NANOS_IN_SECOND;
                fps = (int)Math.round(currentFps / seconds);
                ups = (int)Math.round(currentUpdates / seconds);
                currentFps = 0;
                currentUpdates = 0;
                statsTimer = statsNow;
            }

            if (!processed) {
                long sleepNanos = Math.min(
                        UPDATE_STEP_NANOS - accumulator,
                        FRAME_STEP_NANOS - frameAccumulator
                );
                if (sleepNanos > 0) {
                    long ms = sleepNanos / 1_000_000L;
                    int ns = (int)(sleepNanos % 1_000_000L);
                    try { Thread.sleep(ms, ns); } catch (InterruptedException ignored) {}
                } else {
                    Thread.yield();
                }
            }
        }
    }



    /*
    @Override
    public void run() {
        final long NANOS_IN_SECOND = 1_000_000_000L;
        final double NANOS_BETWEEN_FRAMES  = NANOS_IN_SECOND / (double) targetFps;
        final double NANOS_BETWEEN_UPDATES = NANOS_IN_SECOND / (double) targetUpdates;

        // Reloj base
        long lastTime = System.nanoTime();

        // Momento del último update/render (para calcular deltaTime y por info)
        long lastUpdate = lastTime;
        long lastFrame  = lastTime;

        // Acumuladores para decidir "¿ya toca update/render?"
        double updateAccumulator = 0.0;
        double frameAccumulator  = 0.0;

        long timer = System.currentTimeMillis();

        while (running) {
            long now = System.nanoTime();
            long elapsedNanos = now - lastTime;
            lastTime = now;

            // Acumulamos el tiempo transcurrido desde la iteración anterior
            updateAccumulator += elapsedNanos;
            frameAccumulator  += elapsedNanos;

            boolean processed = false;

            // Update (actualización de físicas del juego)
            if (updateAccumulator >= NANOS_BETWEEN_UPDATES) {
                double deltaTime = (now - lastUpdate) / (double) NANOS_IN_SECOND;
                processInput();
                update(deltaTime);

                lastUpdate = now;
                // Consumimos solo un "slot" de update del acumulador
                updateAccumulator -= NANOS_BETWEEN_UPDATES;
                processed = true;
            }

            // Render (pintamos el mundo)
            if (frameAccumulator >= NANOS_BETWEEN_FRAMES) {
                render();
                lastFrame = now;
                // Consumimos solo un "slot" de frame del acumulador
                frameAccumulator -= NANOS_BETWEEN_FRAMES;

                processed = true;

                if (System.currentTimeMillis() - timer >= 1000) {
                    timer += 1000;
                    fps = currentFps;
                    ups = currentUpdates;
                    currentFps = 0;
                    currentUpdates = 0;
                }
            }

            // Para evitar un consumo excesivo de CPU
            if (!processed) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) { }
            }
        }
    }
     */

    /**
     * Renderiza un frame usando la BufferStrategy.
     */
    private void render() {
        if (bufferStrategy == null) {
            // Puede ocurrir en casos raros (por ejemplo tras un resize)
            createBufferStrategy(2);
            bufferStrategy = getBufferStrategy();
            return;
        }

        do {
            do {
                Graphics2D g = null;
                try {
                    g = (Graphics2D) bufferStrategy.getDrawGraphics();
                    currentFps++;
                    draw(g);
                } finally {
                    if (g != null) {
                        g.dispose();
                    }
                }
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
            // Sincronización para evitar tearing en algunos sistemas
            Toolkit.getDefaultToolkit().sync();
        } while (bufferStrategy.contentsLost());
    }

    public synchronized void stop() {
        if (running) {
            running = false;
            try {
                if (gameThread != null) {
                    gameThread.join();
                }
            } catch (InterruptedException ignored) { }
        }
    }
}
