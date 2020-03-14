package de.simagdo.engine.openglWrapper.openglObjects;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;

public class Attribute {

    protected final int attributeNumber;
    protected final int dataType;
    protected final boolean normalized;
    protected final int componentCount;
    protected final int bytesPerVertex;

    public Attribute(int attrNumber, int dataType, int componentCount) {
        this.attributeNumber = attrNumber;
        this.dataType = dataType;
        this.componentCount = componentCount;
        this.normalized = false;
        this.bytesPerVertex = calcBytesPerVertex();
    }

    public Attribute(int attrNumber, int dataType, int componentCount, boolean normalized) {
        this.attributeNumber = attrNumber;
        this.dataType = dataType;
        this.componentCount = componentCount;
        this.normalized = normalized;
        this.bytesPerVertex = calcBytesPerVertex();
    }

    protected void enable(boolean enable) {
        if (enable) {
            GL20.glEnableVertexAttribArray(this.attributeNumber);
        } else {
            GL20.glDisableVertexAttribArray(this.attributeNumber);
        }
    }

    protected void link(int offset, int stride) {
        GL20.glVertexAttribPointer(this.attributeNumber, this.componentCount, this.dataType, this.normalized, stride, offset);
    }

    private int calcBytesPerVertex() {
        if (this.dataType == GL11.GL_FLOAT || this.dataType == GL11.GL_UNSIGNED_INT || this.dataType == GL11.GL_INT) {
            return 4 * this.componentCount;
        } else if (this.dataType == GL11.GL_SHORT || this.dataType == GL11.GL_UNSIGNED_SHORT) {
            return 2 * componentCount;
        } else if (this.dataType == GL11.GL_BYTE || this.dataType == GL11.GL_UNSIGNED_BYTE) {
            return this.componentCount;
        } else if (this.dataType == GL12.GL_UNSIGNED_INT_2_10_10_10_REV) {
            return 4;
        }
        System.err.println("Unsupported data type for VAO attribute: " + this.dataType);
        return 0;
    }

}
