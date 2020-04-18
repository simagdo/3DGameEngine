package de.simagdo.engine.world;

import de.simagdo.engine.Scene;
import de.simagdo.engine.graph.HeightMapMesh;
import de.simagdo.engine.graph.Material;
import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.graph.text.Texture;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.loaders.assimp.StaticMeshesLoader;
import de.simagdo.engine.world.chunk.Chunk;
import de.simagdo.engine.world.tile.Tile;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

public class World implements IWorld {

    private HashMap<String, Chunk> chunks = new HashMap<>();
    private Scene scene;

    public World(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void generateWorld() throws Exception {
        Chunk chunk = new Chunk(0, 0);
        chunk.createChunk();
        this.chunks.put("0_0", chunk);
    }

    @Override
    public void loadWorld() throws Exception {
        Chunk chunk = this.chunks.get("0_0");
        GameItem[] gameItems = new GameItem[Chunk.CHUNKS_PER_FILE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE];
        Tile[][][] tiles = chunk.getTiles();
        int i = 0;
        for (Tile[][] chunkTile : tiles) {
            for (Tile[] value : chunkTile) {
                for (Tile tile : value) {
                    if (tile.getGameItem() != null) {
                        //System.out.println("Index: " + i);
                        gameItems[i] = tile.getGameItem();
                    }
                    i++;
                }
            }
        }

        this.scene.setGameItems(gameItems);

        /*float reflectance = 1f;

        float blockScale = 0.5f;
        float skyBoxScale = 100.0f;
        float extension = 2.0f;

        float startx = extension * (-skyBoxScale + blockScale);
        float startz = extension * (skyBoxScale - blockScale);
        float starty = -1.0f;
        float inc = blockScale * 2;

        float posx = startx;
        float posz = startz;
        float incy = 0.0f;

        ByteBuffer buf;
        int width;
        int height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            URL url = Texture.class.getResource("/textures/heightmap.png");
            File file = Paths.get(url.toURI()).toFile();
            String filePath = file.getAbsolutePath();
            buf = stbi_load(filePath, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        int instances = height * width;
        System.out.println("Instances: " + instances);
        Mesh mesh = StaticMeshesLoader.load("models/cube.obj", "", instances)[0];
        Texture texture = new Texture("/textures/terrain_textures.png", 2, 1);
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);
        GameItem[] gameItems = new GameItem[instances];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                GameItem gameItem = new GameItem(mesh);
                gameItem.setScale(blockScale);
                int rgb = HeightMapMesh.getRGB(i, j, width, buf);
                incy = rgb / (10 * 255 * 255);
                gameItem.setPosition(posx, starty + incy, posz);
                int textPos = Math.random() > 0.5f ? 0 : 1;
                gameItem.setTextPos(textPos);
                gameItems[i * width + j] = gameItem;

                posx += inc;
            }
            posx = startx;
            posz -= inc;
        }
        scene.setGameItems(gameItems);*/

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
