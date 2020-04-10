package de.simagdo.engine.world.chunk;

import de.simagdo.engine.world.tile.Tile;

import java.util.ArrayList;
import java.util.Arrays;

public class Chunk {

    private static final int CHUNK_SIZE = 32;
    private final int absoluteX;
    private final int absoluteY;
    private final Tile[][] tiles = new Tile[CHUNK_SIZE][CHUNK_SIZE];

    public Chunk(int absoluteX, int absoluteY) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
    }

    public int getAbsoluteX() {
        return absoluteX;
    }

    public int getAbsoluteY() {
        return absoluteY;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "absoluteX=" + absoluteX +
                ", absoluteY=" + absoluteY +
                ", tiles=" + Arrays.toString(tiles) +
                '}';
    }
}
