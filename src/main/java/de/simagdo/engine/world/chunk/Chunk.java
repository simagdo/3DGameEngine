package de.simagdo.engine.world.chunk;

import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.GameItemType;
import de.simagdo.engine.loaders.assimp.StaticMeshesLoader;
import de.simagdo.engine.world.tile.Tile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Chunk implements IChunk {

    public static final int CHUNK_SIZE = 16;
    public static final int CHUNKS_PER_FILE = 1;
    private final int absoluteX;
    private final int absoluteY;
    private final Tile[][][] tiles = new Tile[CHUNKS_PER_FILE][CHUNK_SIZE][CHUNK_SIZE];

    public Chunk(int absoluteX, int absoluteY) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
    }

    public void createChunk() throws Exception {
        String objFile = "models/cube.obj";
        String texture = "/textures/grassblock.png";
        Mesh mesh = StaticMeshesLoader.load("models/cube.obj", "/textures/grassblock.png")[0];
        GameItem gameItem = new GameItem(mesh);
        for (int chunk = 0; chunk < CHUNKS_PER_FILE; chunk++) {
            for (int x = 0; x < CHUNK_SIZE; x++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    int random = (int) Math.round(Math.random());
                    //String objFile = random == 0 ? "models/cube.obj" : "";
                    //String texture = random == 0 ? "/textures/grassblock.png" : "";
                    if (random == 0) {
                        gameItem.setPosition(x, 0, z);
                        Tile tile = new Tile(objFile, texture, gameItem, GameItemType.TREE_LARGE);
                        this.tiles[chunk][x][z] = tile;
                    } else {
                        Tile tile = new Tile();
                        this.tiles[chunk][x][z] = tile;
                    }
                }
            }
        }

        //Save Chunks in JSON File
        this.saveChunksToJSON();

    }

    @Override
    public void saveChunksToJSON() {
        File file = new File("C:\\Users\\simag\\IdeaProjects\\3DGameEngine\\src\\main\\resources\\\\world\\chunk_" + this.absoluteX + "_" + this.absoluteY + ".json");
        JSONObject main = new JSONObject();
        JSONArray chunks = new JSONArray();
        JSONArray posX = new JSONArray();
        JSONArray posY = new JSONArray();
        JSONObject position = new JSONObject();
        JSONObject positions = new JSONObject();

        /*for (int chunk = 0; chunk < CHUNKS_PER_FILE; chunk++) {
            main.put("CHUNK_" + this.absoluteX + "_" + this.absoluteY, chunks);
            for (int i = 0; i < CHUNK_SIZE; i++) {
                JSONObject positionX = new JSONObject();
                JSONArray positionY = new JSONArray();

                for (int j = 0; j < CHUNK_SIZE; j++) {

                }

                positionX.put("PositionX_" + i, "Test");
                chunks.add(positionX);
            }
        }*/

        int chunkPosition = 0;
        int positionX = 0;
        int positionY = 0;
        for (Tile[][] chunkTile : this.tiles) {

            main.put("CHUNK_" + chunkPosition, chunks);
            chunkPosition++;

            for (Tile[] value : chunkTile) {

                posX.add("PositionX_" + position);

                //chunks.add("PositionX_" + positionX);
                //chunks.add(positions);

                positions.put("PositionX_" + positionX, position);

                for (Tile tile : value) {

                }
            }
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(main.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadChunksFromJSON() throws Exception {

    }

    public int getAbsoluteX() {
        return absoluteX;
    }

    public int getAbsoluteY() {
        return absoluteY;
    }

    public Tile[][][] getTiles() {
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
