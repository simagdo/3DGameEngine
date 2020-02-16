package de.simagdo.engine.loaders.assimp;

import de.simagdo.engine.graph.text.Texture;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    private static TextureCache INSTANCE;
    private Map<String, Texture> texturesMap;

    private TextureCache() {
        this.texturesMap = new HashMap<>();
    }

    public static synchronized TextureCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TextureCache();
        }
        return INSTANCE;
    }

    public Texture getTexture(String path) throws Exception {
        Texture texture = this.texturesMap.get(path);
        if (texture == null) {
            texture = new Texture(path);
            this.texturesMap.put(path, texture);
        }
        return texture;
    }

}
