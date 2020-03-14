package de.simagdo.engine.openglWrapper.openglObjects;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL31;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VBO {

    private final int vboId;
    private final int type;
    private final int usage;
    private long sizeInBytes;

    private VBO(int vboId, int type, int usage) {
        this.vboId = vboId;
        this.type = type;
        this.usage = usage;
        this.bind();
    }

    public static VBO create(int type, int usage) {
        int id = GL15.glGenBuffers();
        return new VBO(id, type, usage);
    }

    public long getByteSize() {
        return this.sizeInBytes;
    }

    public void bind() {
        GL15.glBindBuffer(this.type, this.vboId);
    }

    public void unbind() {
        GL15.glBindBuffer(this.type, 0);
    }

    public void resize(long copyDataSize, long newSizeInBytes) {
        VBO temporaryVBO = this.createTempVBO(copyDataSize);
        this.copyContentsTo(temporaryVBO, 0, 0, copyDataSize);
        this.allocateData(newSizeInBytes, GL31.GL_COPY_READ_BUFFER);
        temporaryVBO.copyContentsTo(this, 0, 0, copyDataSize);
        temporaryVBO.delete();
    }

    private VBO createTempVBO(long datSize) {
        VBO tempVBO = VBO.create(GL31.GL_COPY_WRITE_BUFFER, GL21.GL_STATIC_COPY);
        tempVBO.allocateData(datSize);
        return tempVBO;
    }

    public void copyContentsTo(VBO destination, long readStart, long writeStart, long dataSize) {
        GL15.glBindBuffer(GL31.GL_COPY_READ_BUFFER, this.vboId);
        GL15.glBindBuffer(GL31.GL_COPY_WRITE_BUFFER, destination.vboId);
        GL31.glCopyBufferSubData(GL31.GL_COPY_READ_BUFFER, GL31.GL_COPY_WRITE_BUFFER, readStart, writeStart, dataSize);
    }

    public void allocateData(long sizeInBytes) {
        this.allocateData(sizeInBytes, this.type);
    }

    public void allocateData(long sizeInBytes, int target) {
        GL15.glBufferData(target, sizeInBytes, this.usage);
        this.sizeInBytes = sizeInBytes;
    }

    public void storeData(long startInBytes, IntBuffer data) {
        GL15.glBufferSubData(this.type, startInBytes, data);
    }

    public void storeData(long startInBytes, FloatBuffer data) {
        GL15.glBufferSubData(this.type, startInBytes, data);
    }

    public void storeData(long startInBytes, ByteBuffer data) {
        GL15.glBufferSubData(this.type, startInBytes, data);
    }

    public void allocateAndStoreData(ByteBuffer data) {
        GL15.glBufferData(this.type, data, this.usage);
        this.sizeInBytes = data.limit();
    }

    public void delete() {
        GL15.glDeleteBuffers(this.vboId);
    }

}
