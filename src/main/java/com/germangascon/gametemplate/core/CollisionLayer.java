package com.germangascon.gametemplate.core;

/**
 * <p><strong>ColisionLayers</strong></p>
 * <p>Descripción</p>
 * License: 🅮 Public Domain<br />
 * Created on: 2025-12-12<br />
 *
 * @author Germán Gascón <ggascon@gmail.com>
 * @version 0.0.1
 * @since 0.0.1
 **/
public class CollisionLayer {
    // Definimos bits para cada tipo de capa
    public static final int LAYER_NONE          = 0;      // 00000
    public static final int LAYER_PLAYER        = 1 << 0; // 00001
    public static final int LAYER_ENEMY         = 1 << 1; // 00010
    public static final int LAYER_PLAYER_BULLET = 1 << 2; // 00100
    public static final int LAYER_ENEMY_BULLET  = 1 << 3; // 01000
    public static final int LAYER_SCENERY       = 1 << 4; // 10000

    // etc
    public static final int LAYER_ALL = 0xFFFFFFFF;
}
