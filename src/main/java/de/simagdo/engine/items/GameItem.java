package de.simagdo.engine.items;

import de.simagdo.engine.graph.Mesh;
import org.joml.Vector3f;

public class GameItem {

    private Mesh mesh;
    private final Vector3f position;
    private float scale;
    private final Vector3f rotation;

    public GameItem() {
        this.position = new Vector3f(0, 0, 0);
        this.scale = 1;
        this.rotation = new Vector3f(0, 0, 0);
    }

    public GameItem(Mesh mesh) {
        this.mesh = mesh;
        this.position = new Vector3f(0, 0, 0);
        this.scale = 1;
        this.rotation = new Vector3f(0, 0, 0);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
