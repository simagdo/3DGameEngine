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
    public static final int CHUNKS_PER_FILE = 9;
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

        JSONObject chunksFile = new JSONObject();
        JSONArray chunks = new JSONArray();

        for (int currentChunk = 0; currentChunk < this.tiles.length; currentChunk++) {
            JSONObject chunk = new JSONObject();
            chunksFile.put("Chunk_" + currentChunk, chunk);

            for (int x = 0; x < this.tiles[currentChunk].length; x++) {
                JSONObject xPosition = new JSONObject();
                chunk.put("PositionX_" + x, xPosition);

                for (int y = 0; y < this.tiles[currentChunk][x].length; y++) {
                    JSONObject yPosition = new JSONObject();
                    xPosition.put("PositionY_" + y, this.saveYPosition(this.tiles[currentChunk][x][y]));

                    //yPosition.put("Test_" + y, y);

                }

            }

        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(chunksFile.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject saveYPosition(Tile tile) {
        JSONObject yPosition = new JSONObject();

        System.out.println(tile.toString());

        if (tile.getGameItem() != null) {
            //yPosition.put("UUID", tile.getGameItem().getUuid().toString());
            yPosition.put("GameItemType", tile.getGameItemType().name());
            yPosition.put("ObjModel", tile.getObjModel());
            yPosition.put("Texture", tile.getTexture());
            yPosition.put("Scale", tile.getGameItem().getScale());
            yPosition.put("Position", this.saveGameItemPosition(tile));
            yPosition.put("Rotation", this.saveGameItemRotation(tile));
        }

        return yPosition;
    }

    /**
     * Save the Position of the {@link GameItem} in the File
     *
     * @param tile which will be saved
     * @return JSONObject which contains the saved Position.
     */
    private JSONObject saveGameItemPosition(Tile tile) {
        JSONObject position = new JSONObject();

        position.put("X", tile.getGameItem().getPosition().x);
        position.put("Y", tile.getGameItem().getPosition().y);
        position.put("Z", tile.getGameItem().getPosition().z);

        return position;
    }

    /**
     * Save the Rotation of the {@link GameItem} in the File
     *
     * @param tile which will be saved
     * @return JSONObject which contains the saved Rotation.
     */
    private JSONObject saveGameItemRotation(Tile tile) {
        JSONObject rotation = new JSONObject();

        rotation.put("X", tile.getGameItem().getRotation().x);
        rotation.put("Y", tile.getGameItem().getRotation().y);
        rotation.put("Z", tile.getGameItem().getRotation().z);

        return rotation;
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
