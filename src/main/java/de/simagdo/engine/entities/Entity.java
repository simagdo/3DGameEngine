package de.simagdo.engine.entities;

import de.simagdo.engine.items.GameItem;
import org.joml.Vector3f;

public abstract class Entity {

    private GameItem gameItem;
    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    public Entity(GameItem gameItem, Vector3f position, Vector3f rotation, float scale) {
        this.gameItem = gameItem;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public abstract void move();

    public abstract void jump();

    public abstract void checkKeyboardInput();

    public void increasePosition(float x, float y, float z) {
        this.increasePosition(new Vector3f(x, y, z));
    }

    public void increasePosition(Vector3f position) {
        this.position.x += position.x;
        this.position.y += position.y;
        this.position.z += position.z;
        this.gameItem.setPosition(this.position.x, this.position.y, this.position.z);
    }

    public void increaseRotation(float x, float y, float z) {
        this.increaseRotation(new Vector3f(x, y, z));
    }

    public void increaseRotation(Vector3f rotation) {
        this.rotation.x += rotation.x;
        this.rotation.y += rotation.y;
        this.rotation.z += rotation.z;
        this.gameItem.setRotation(this.rotation.x, this.rotation.y, this.rotation.z);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

}
