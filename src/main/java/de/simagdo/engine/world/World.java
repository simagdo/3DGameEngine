package de.simagdo.engine.world;

import de.simagdo.engine.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.HashMap;

public class World implements IWorld {

    private HashMap<String, Chunk> chunks = new HashMap<>();

    @Override
    public World generateWorld() {
        return null;
    }

    @Override
    public World loadWorld() {
        return null;
    }

    @Override
    public void saveWorld() {

    }

    @Override
    public ArrayList<Chunk> loadChunks() {
        return null;
    }

    @Override
    public Chunk loadChunk(String key) {
        return null;
    }

    @Override
    public void saveChunks() {

    }

    @Override
    public void saveChunk(String key) {

    }

    @Override
    public void updateChunk(String key) {

    }
}
