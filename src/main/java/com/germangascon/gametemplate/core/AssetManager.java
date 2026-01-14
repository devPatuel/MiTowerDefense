package com.germangascon.gametemplate.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AssetManager {
    private final Map<String, BufferedImage> sprites;

    public AssetManager() {
        sprites = new HashMap<>();
    }

    public void loadSprite(String spritePath) throws IOException {
        BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(spritePath)));
        sprites.put(spritePath, image);
    }

    public boolean containsSprite(String spriteName) {
        return sprites.containsKey(spriteName);
    }

    public BufferedImage getSprite(String spriteName) {
        return sprites.get(spriteName);
    }
}
