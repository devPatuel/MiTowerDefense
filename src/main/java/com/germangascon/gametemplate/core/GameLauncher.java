package com.germangascon.gametemplate.core;

import javax.swing.*;
import java.awt.*;

/**
 * <p><strong>GameLauncher</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-12<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public class GameLauncher {
    public GameLauncher(String title, GameScene defaultScene, int width, int height, int tileSize, int targetFps, int targetUpdates) {
        Engine engine = new Engine(defaultScene, width, height, tileSize, targetFps, targetUpdates);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();
        // Si existe un monitor externo lo seleccionamos
        GraphicsDevice externalMonitor = devices.length > 1 ? devices[1] : devices[0];
        GraphicsConfiguration gc = externalMonitor.getDefaultConfiguration();

        JFrame frame = new JFrame(title, gc);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(engine);
        frame.pack();
        // frame.setLocationRelativeTo(null); // centra en la pantalla pero da problemas con doble monitor

        // Centramos manualmente en la pantalla
        Rectangle bounds = gc.getBounds();
        int x = bounds.x + (bounds.width  - frame.getWidth())  / 2;
        int y = bounds.y + (bounds.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);

        frame.setResizable(false);

        frame.setVisible(true);

        // Importante para que reciba eventos de teclado
        engine.requestFocus();
    }
}
