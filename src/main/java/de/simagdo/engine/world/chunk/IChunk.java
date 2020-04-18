package de.simagdo.engine.world.chunk;

public interface IChunk {

    public void createChunk() throws Exception;

    public void saveChunksToJSON() throws Exception;

    public void loadChunksFromJSON() throws Exception;

}
