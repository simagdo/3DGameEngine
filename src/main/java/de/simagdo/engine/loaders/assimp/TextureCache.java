package de.simagdo.engine.loaders.assimp;

import de.simagdo.engine.graph.text.Texture;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    private static TextureCache INSTANCE;
    private Map<String, Texture> textureMap;

    private TextureCache() {
        this.textureMap = new HashMap<>();
    }

    public static synchronized TextureCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TextureCache();
        }
        return INSTANCE;
    }

    public Texture getTexture(String path) throws Exception {
        Texture texture = this.textureMap.get(path);
        if (texture == null) {
            texture = new Texture(path);
            this.textureMap.put(path, texture);
        }
        return texture;
    }

}
