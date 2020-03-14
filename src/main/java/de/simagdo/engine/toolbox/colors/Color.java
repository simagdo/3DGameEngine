package de.simagdo.engine.toolbox.colors;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Color {

    private Vector3f color = new Vector3f();
    private float a = 1;

    public Color() {

    }

    public Color(float r, float g, float b) {
        this.color.set(r, g, b);
    }

    public Color(Vector3f color) {
        this.color.set(color);
    }

    public Color(float r, float g, float b, float a) {
        this.color.set(r, g, b);
        this.a = a;
    }

    public Color(float r, float g, float b, boolean convert) {
        if (convert) this.color.set(r / 255f, g / 255f, b / 255f);
        else this.color.set(r, g, b);
    }

    public Vector3f getColor() {
        return color;
    }

    public float getR() {
        return this.color.x;
    }

    public float getG() {
        return this.color.y;
    }

    public float getB() {
        return this.color.z;
    }

    public float getA() {
        return a;
    }

    public byte[] asByteBuffer() {
        return new byte[]{(byte) (this.color.x * 255), (byte) (this.color.y * 255), (byte) (this.color.z * 255), (byte) (this.a * 255)};
    }

    public FloatBuffer getAsFloatBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
        buffer.put(new float[]{this.color.x, this.color.y, this.color.z, this.a});
        buffer.flip();
        return buffer;
    }

    public Color duplicate() {
        return new Color(this.color.x, this.color.y, this.color.z, this.a);
    }

    public void multiplyBy(Color color) {
        this.color.x *= color.color.x;
        this.color.y *= color.color.y;
        this.color.z *= color.color.z;
    }

    public void setColor(float r, float g, float b) {
        this.color.set(r, g, b);
    }

    public void setColor(Vector3f color) {
        this.color.set(color);
    }

    public void setColor(Color color) {
        this.color.set(color.color);
        this.a = color.a;
    }

}
