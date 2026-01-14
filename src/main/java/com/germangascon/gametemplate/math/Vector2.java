package com.germangascon.gametemplate.math;

/**
 * <p><strong>Vector2</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-10<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public class Vector2 {
    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2() {
        this(0, 0);
    }

    public Vector2(Vector2 v) {
        set(v);
    }

    public static Vector2 zero() {
        return new Vector2(0, 0);
    }

    public static Vector2 up() {
        return new Vector2(0, -1);
    }

    public static Vector2 down() {
        return new Vector2(0, 1);
    }

    public static Vector2 left() {
        return new Vector2(-1, 0);
    }

    public static Vector2 right() {
        return new Vector2(1, 0);
    }

    public Vector2 set(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public float len(){
        return (float)Math.sqrt(x * x + y * y);
    }

    public static float len(float x, float y) {
        return (float)Math.sqrt(x * x + y * y);
    }

    public float len2() {
        return x * x + y * y;
    }

    public Vector2 sub(Vector2 v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public Vector2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2 add(Vector2 v) {
        x += v.x;
        y += v.y;
        return this;
    }

    public Vector2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2 mulAdd(Vector2 vec, float scalar) {
        this.x += vec.x * scalar;
        this.y += vec.y * scalar;
        return this;
    }

    public float distance(Vector2 v) {
        final float dx = v.x - x;
        final float dy = v.y - y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public float distance2(Vector2 v) {
        float dx = v.x - x;
        float dy = v.y - y;
        return dx * dx + dy * dy;
    }

    public float distance(float x, float y) {
        final float dx = x - this.x;
        final float dy = y - this.y;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public float distance2(float x, float y) {
        float dx = x - this.x;
        float dy = y - this.y;
        return dx * dx + dy * dy;
    }

    public Vector2 nor() {
        float length = len();
        if (length != 0f) {
            this.x /= length;
            this.y /= length;
        }
        return this;
    }

    public Vector2 scl(float s) {
        this.x *= s;
        this.y *= s;
        return this;
    }

    public Vector2 directionTo(Vector2 target) {
        float dx = target.x - this.x;
        float dy = target.y - this.y;

        float len = (float) Math.sqrt(dx * dx + dy * dy);

        if (len == 0f) {
            return new Vector2(0f, 0f); // dirección nula
        }
        return new Vector2(dx / len, dy / len);
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Vector2 vector2)) return false;

        return Float.compare(x, vector2.x) == 0 && Float.compare(y, vector2.y) == 0;
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(x);
        result = 31 * result + Float.hashCode(y);
        return result;
    }

    @Override
    public String toString () {
        return "(" + x + "," + y + ")";
    }
}
