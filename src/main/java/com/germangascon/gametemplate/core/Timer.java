package com.germangascon.gametemplate.core;

/**
 * <p><strong>Timer</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-13<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public class Timer {
    private float elapsed = 0f;

    public void update(double deltaTime) {
        elapsed += (float) deltaTime;
    }

    public void reset() {
        elapsed = 0f;
    }

    public boolean every(float seconds) {
        if (elapsed >= seconds) {
            elapsed -= seconds;
            return true;
        }
        return false;
    }

    public boolean ready(float seconds) {
        return elapsed >= seconds;
    }

    public boolean after(float seconds) {
        return elapsed >= seconds;
    }
}

