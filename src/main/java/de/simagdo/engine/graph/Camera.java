package de.simagdo.engine.graph;

import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    private final Vector3f rotation;

    public Camera() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            this.position.x += (float) Math.sin(Math.toRadians(this.rotation.y)) * -1.0f * offsetZ;
            this.position.z += (float) Math.cos(Math.toRadians(this.rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            this.position.x += (float) Math.sin(Math.toRadians(this.rotation.y - 90)) * -1.0f * offsetZ;
            this.position.z += (float) Math.cos(Math.toRadians(this.rotation.y - 90)) * offsetZ;
        }

        position.y += offsetY;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        this.rotation.x = +offsetX;
        this.rotation.y = +offsetY;
        this.rotation.z = +offsetZ;
    }

}
