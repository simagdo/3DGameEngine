package de.simagdo.engine.graph;

import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.graph.text.Texture;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Mesh {

    private final int vaoId;
    private final List<Integer> vboIdList;
    private final int vertexCount;
    private Material material;

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;
            this.vboIdList = new ArrayList();

            this.vaoId = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(this.vaoId);

            // Position VBO
            int vboId = GL15.glGenBuffers();
            this.vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, posBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = GL15.glGenBuffers();
            this.vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(textCoords).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textCoordsBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = GL15.glGenBuffers();
            this.vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vecNormalsBuffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = GL15.glGenBuffers();
            this.vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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

    public int getVaoId() {
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

    private void initRender() {
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

    }

    private void endRender() {
        //Restore State
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void cleanUp() {
        GL20.glDisableVertexAttribArray(0);

        // Delete the VBOs
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        for (int vboId : this.vboIdList) GL15.glDeleteBuffers(vboId);

        //Delete the VAOs
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(this.vaoId);
    }

}