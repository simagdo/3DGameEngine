package de.simagdo.engine.items;

public class Terrain {

    private final GameItem[] gameItems;

    public Terrain(int blocksPerRow, float scale, float minY, float maxY, String heightMap, String textureFile, int textInc)throws Exception{
        gameItems=new GameItem[blocksPerRow*blocksPerRow];

    }

    public GameItem[] getGameItems() {
        return gameItems;
    }
}