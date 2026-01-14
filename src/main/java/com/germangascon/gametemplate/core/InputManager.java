package com.germangascon.gametemplate.core;

import java.awt.event.*;
import java.util.Arrays;

/**
 * <p><strong>Input</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-11<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public class InputManager implements KeyListener, MouseListener, MouseMotionListener {
    // Input de teclado (escrito por EDT, leído por gameThread)
    // Estado de teclas: true si está pulsada actualmente
    private final boolean[] keys;
    // Opcional: teclas "just pressed" (pulsadas este frame)
    private final boolean[] keysJustPressed;

    // Input de ratón (escrito por EDT, leído por gameThread)
    private volatile boolean leftClick;
    private volatile boolean rightClick;
    private volatile boolean middleClick;
    private volatile int mouseX;
    private volatile int mouseY;

    public InputManager() {
        keys = new boolean[256];
        keysJustPressed = new boolean[256];
        mouseX =  0;
        mouseY = 0;
        leftClick = false;
        rightClick = false;
        middleClick = false;
    }

    public boolean isKeyDown(int keyCode) {
        return keyCode >= 0 && keyCode < keys.length && keys[keyCode];
    }

    public boolean isKeyJustPressed(int keyCode) {
        return keyCode >= 0 && keyCode < keysJustPressed.length && keysJustPressed[keyCode];
    }

    public boolean hasLeftClick() {
        return leftClick;
    }

    public boolean hasRightClick() {
        return rightClick;
    }

    public boolean hasMiddleClick() {
        return middleClick;
    }

    public void consumeLeftClick() {
        leftClick = false;
    }

    public void consumeRightClick() {
        rightClick = false;
    }

    public void consumeMiddleClick() {
        middleClick = false;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public void reset() {
        Arrays.fill(keysJustPressed, false);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        // BUTTON1 -> clic izquierdo
        // BUTTON2 -> clic central (rueda)
        // BUTTON3 -> clic derecho
        switch (e.getButton()) {
            case MouseEvent.BUTTON1 -> leftClick = true;
            case MouseEvent.BUTTON2 -> middleClick = true;
            case MouseEvent.BUTTON3 -> rightClick = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // No suele utilizarse en juegos
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No suele utilizarse en juegos (es para texto, respeta repetición, layout del teclado, etc.)
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < keys.length) {
            if (!keys[code]) {
                // Solo marcamos como "just pressed" cuando pasa de no pulsada a pulsada
                keysJustPressed[code] = true;
            }
            // Marcamos como pulsada
            keys[code] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < keys.length) {
            keys[code] = false;
            // No hace falta tocar keysJustPressed aquí. Las ponemos todas a false al final del processInput
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
