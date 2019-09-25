package de.simagdo.engine.items;

import de.simagdo.engine.graph.HeightMapMesh;
import de.simagdo.engine.graph.text.Texture;
import org.joml.Vector3f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.stb.STBImage.*;

public class Terrain {

    private final GameItem[] gameItems;
    private final Box2D[][] boundingBoxes;
    private final int terrainSize;
    private final int verticesPerRow;
    private final int verticesPerCol;
    private final HeightMapMesh heightMapMesh;

    /**
     * A Terrain is composed by blocks, each block is a GameItem constructed
     * from a HeightMap.
     *
     * @param terrainSize   The number of blocks will be terrainSize * terrainSize
     * @param scale         The scale to be applied to each terrain block
     * @param minY          The minimum y value, before scaling, of each terrain block
     * @param maxY          The maximum y value, before scaling, of each terrain block
     * @param heightMapFile Where the HeightMap is located
     * @param textureFile   Which will be used
     * @param textInc
     * @throws Exception
     */
    public Terrain(int terrainSize, float scale, float minY, float maxY, String heightMapFile, String textureFile, int textInc) throws Exception {
        this.gameItems = new GameItem[terrainSize * terrainSize];
        this.terrainSize = terrainSize;

        ByteBuffer buffer;
        int width;
        int height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            URL url = Texture.class.getResource(heightMapFile);
            File file = Paths.get(url.toURI()).toFile();
            String filePath = file.getAbsolutePath();
            buffer = stbi_load(filePath, w, h, channels, 4);
            if (buffer == null)
                throw new Exception("Image file[" + filePath + "] not loaded: " + stbi_failure_reason());
            width = w.get();
            height = h.get();
        }

        //The number of Vertices per Column and Row
        this.verticesPerRow = width - 1;
        this.verticesPerCol = height - 1;

        this.heightMapMesh = new HeightMapMesh(minY, maxY, buffer, width, height, textureFile, textInc);
        this.boundingBoxes = new Box2D[this.terrainSize][this.terrainSize];

        for (int row = 0; row < terrainSize; row++) {
            for (int col = 0; col < terrainSize; col++) {
                float xDisplacement = (col - ((float) terrainSize - 1) / (float) 2) * scale * HeightMapMesh.getXLength();
                float zDisplacement = (row - ((float) terrainSize - 1) / (float) 2) * scale * HeightMapMesh.getZLength();

                GameItem terrainBlock = new GameItem(this.heightMapMesh.getMesh());
                terrainBlock.setScale(scale);
                terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
                this.gameItems[row * terrainSize + col] = terrainBlock;
                this.boundingBoxes[row][col] = getBoundingBox(terrainBlock);
            }
        }

        stbi_image_free(buffer);

    }

    private Box2D getBoundingBox(GameItem terrainBlock) {
        float scale = terrainBlock.getScale();
        Vector3f position = terrainBlock.getPosition();

        float topLeftX = HeightMapMesh.STARTX * scale + position.x;
        float topLeftZ = HeightMapMesh.STARTZ * scale + position.z;
        float width = Math.abs(HeightMapMesh.STARTX * 2) * scale;
        float height = Math.abs(HeightMapMesh.STARTZ * 2) * scale;
        return new Box2D(topLeftX, topLeftZ, width, height);
    }

    public float getHeight(Vector3f position) {
        float result = Float.MIN_VALUE;

        //For each Terrain Block we get the Bounding Box, translate it to view Coordinates and check if the Position is contained in that Bounding Box
        Box2D boundingBox = null;
        boolean found = false;
        GameItem terrainBlock = null;

        for (int row = 0; row < this.terrainSize && !found; row++) {
            for (int col = 0; col < this.terrainSize && !found; col++) {
                terrainBlock = this.gameItems[row * this.terrainSize + col];
                boundingBox = this.boundingBoxes[row][col];
                found = boundingBox.contains(position.x, position.z);
            }
        }

        //If we have found a Terrain Block that contains the Position  we need to calculate the Height of the Terrain on that Position
        if (found) {
            Vector3f[] triangle = getTriangle(position, boundingBox, terrainBlock);
            result = interpolateHeight(triangle[0], triangle[1], triangle[2], position.x, position.z);
        }

        return result;
    }

    protected Vector3f[] getTriangle(Vector3f position, Box2D boundingBox, GameItem terrainBlock) {
        // Get the column and row of the heightmap associated to the current position
        float cellWidth = boundingBox.getWidth() / (float) verticesPerCol;
        float cellHeight = boundingBox.getHeight() / (float) verticesPerRow;
        int col = (int) ((position.x - boundingBox.getX()) / cellWidth);
        int row = (int) ((position.z - boundingBox.getY()) / cellHeight);

        Vector3f[] triangle = new Vector3f[3];
        triangle[1] = new Vector3f(
                boundingBox.getX() + col * cellWidth,
                getWorldHeight(row + 1, col, terrainBlock),
                boundingBox.getY() + (row + 1) * cellHeight);
        triangle[2] = new Vector3f(
                boundingBox.getX() + (col + 1) * cellWidth,
                getWorldHeight(row, col + 1, terrainBlock),
                boundingBox.getY() + row * cellHeight);
        if (position.z < getDiagonalZCoord(triangle[1].x, triangle[1].z, triangle[2].x, triangle[2].z, position.x)) {
            triangle[0] = new Vector3f(
                    boundingBox.getX() + col * cellWidth,
                    getWorldHeight(row, col, terrainBlock),
                    boundingBox.getY() + row * cellHeight);
        } else {
            triangle[0] = new Vector3f(
                    boundingBox.getX() + (col + 1) * cellWidth,
                    getWorldHeight(row + 2, col + 1, terrainBlock),
                    boundingBox.getY() + (row + 1) * cellHeight);
        }

        return triangle;
    }

    protected float getDiagonalZCoord(float x1, float z1, float x2, float z2, float x) {
        float z = ((z1 - z2) / (x1 - x2)) * (x - x1) + z1;
        return z;
    }

    protected float getWorldHeight(int row, int col, GameItem gameItem) {
        float y = heightMapMesh.getHeight(row, col);
        return y * gameItem.getScale() + gameItem.getPosition().y;
    }

    protected float interpolateHeight(Vector3f pA, Vector3f pB, Vector3f pC, float x, float z) {
        // Plane equation ax+by+cz+d=0
        float a = (pB.y - pA.y) * (pC.z - pA.z) - (pC.y - pA.y) * (pB.z - pA.z);
        float b = (pB.z - pA.z) * (pC.x - pA.x) - (pC.z - pA.z) * (pB.x - pA.x);
        float c = (pB.x - pA.x) * (pC.y - pA.y) - (pC.x - pA.x) * (pB.y - pA.y);
        float d = -(a * pA.x + b * pA.y + c * pA.z);
        // y = (-d -ax -cz) / b
        float y = (-d - a * x - c * z) / b;
        return y;
    }

    public GameItem[] getGameItems() {
        return gameItems;
    }
}