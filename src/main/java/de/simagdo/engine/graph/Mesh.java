package de.simagdo.engine.graph;

import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.graph.text.Texture;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Mesh {

    protected final int vaoId;
    protected final List<Integer> vboIdList;
    private final int vertexCount;
    private Material material;
    public static final int MAX_WEIGHTS = 4;

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        this(positions, textCoords, normals, indices, createEmptyIntArray(MAX_WEIGHTS * positions.length / 3, 0), createEmptyFloatArray(MAX_WEIGHTS * positions.length / 3, 0));
    }

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, int[] jointIndices, float[] weights) {
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer weightsBuffer = null;
        IntBuffer jointIndicesBuffer = null;
        try {
            vertexCount = indices.length;
            this.vboIdList = new ArrayList();

            this.vaoId = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(this.vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            this.vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL_ARRAY_BUFFER, posBuffer, GL15.GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = glGenBuffers();
            this.vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL15.GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = glGenBuffers();
            this.vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL15.GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Weights
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            weightsBuffer = MemoryUtil.memAllocFloat(weights.length);
            weightsBuffer.put(weights).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);

            // Joint indices
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            jointIndicesBuffer = MemoryUtil.memAllocInt(jointIndices.length);
            jointIndicesBuffer.put(jointIndices).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, jointIndicesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(4, 4, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            this.vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (weightsBuffer != null) {
                MemoryUtil.memFree(weightsBuffer);
            }
            if (jointIndicesBuffer != null) {
                MemoryUtil.memFree(jointIndicesBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public final int getVaoId() {
        return this.vaoId;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public void render() {
        this.initRender();

        GL11.glDrawElements(GL11.GL_TRIANGLES, this.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        this.endRender();
    }

    public void renderList(List<GameItem> gameItems, Consumer<GameItem> consumer) {
        this.initRender();

        for (GameItem gameItem : gameItems) {
            //Set up Data required by GameItem
            consumer.accept(gameItem);

            //Render this GameItem
            GL11.glDrawElements(GL11.GL_TRIANGLES, this.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        }

        this.endRender();
    }

    protected void initRender() {
        Texture texture = this.material.getTexture();
        if (texture != null) {
            //Activate first texture Bank
            GL13.glActiveTexture(GL13.GL_TEXTURE0);

            //Bind the Texture
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());
        }

        Texture normalMap = this.material.getNormalMap();
        if (normalMap != null) {
            //Activate first texture bank
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            //Bind the Texture
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMap.getId());
        }

        // Draw the mesh
        GL30.glBindVertexArray(this.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        GL20.glEnableVertexAttribArray(4);

    }

    protected void endRender() {
        //Restore State
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL20.glDisableVertexAttribArray(4);
        GL30.glBindVertexArray(0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void cleanUp() {
        GL20.glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : this.vboIdList) {
            GL15.glDeleteBuffers(vboId);
        }

        // Delete the text
        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanUp();
        }

        // Delete the VAO
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(this.vaoId);
    }

    public void deleteBuffers() {
        GL20.glDisableVertexAttribArray(0);

        //Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : this.vboIdList) GL15.glDeleteBuffers(vboId);

        //Delete the VAOs
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(this.vaoId);
    }

    protected static float[] createEmptyFloatArray(int length, float defaultValue) {
        float[] result = new float[length];
        Arrays.fill(result, defaultValue);
        return result;
    }

    protected static int[] createEmptyIntArray(int length, int defaultValue) {
        int[] result = new int[length];
        Arrays.fill(result, defaultValue);
        return result;
    }

}