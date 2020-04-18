package de.simagdo.engine.world;

import de.simagdo.engine.world.chunk.Chunk;

import java.util.ArrayList;

public interface IWorld {

    public void generateWorld() throws Exception;

    public void loadWorld() throws Exception;

    public void saveWorld();

    public ArrayList<Chunk> loadChunks();

    public Chunk loadChunk(String key);

    public void saveChunks();

    public void saveChunk(String key);

    public void updateChunk(String key);

}
