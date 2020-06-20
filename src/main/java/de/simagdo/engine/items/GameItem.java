package de.simagdo.engine.items;

import de.simagdo.engine.graph.Mesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.UUID;

public class GameItem implements Serializable {

    private Mesh[] meshes;
    private final Vector3f position;
    private float scale;
    private final Quaternionf rotation;
    private int textPos;
    private boolean selected;
    private boolean insideFrustum;
    private boolean disableFrustumCulling;
    private UUID uuid;

    public GameItem() {
        this.position = new Vector3f();
        this.scale = 1;
        this.rotation = new Quaternionf();
        this.textPos = 0;
        this.selected = false;
        this.insideFrustum = true;
        this.disableFrustumCulling = false;
        this.uuid = UUID.randomUUID();
    }

    public GameItem(Mesh mesh) {
        this();
        this.meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes) {
        this();
        this.meshes = meshes;
    }

    public Mesh getMesh() {
        return this.meshes[0];
    }

    public Mesh[] getMeshes() {
        return this.meshes;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Quaternionf getRotation() {
        return this.rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.setRotation(new Quaternionf(x, y, z, 0));
    }

    public void setRotation(Quaternionf quaternionf) {
        this.rotation.set(quaternionf);
    }

    public void setMeshes(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }

    public void setMesh(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }

    public int getTextPos() {
        return textPos;
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }

    public void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isInsideFrustum() {
        return insideFrustum;
    }

    public void setInsideFrustum(boolean insideFrustum) {
        this.insideFrustum = insideFrustum;
    }

    public boolean isDisableFrustumCulling() {
        return disableFrustumCulling;
    }

    public void setDisableFrustumCulling(boolean disableFrustumCulling) {
        this.disableFrustumCulling = disableFrustumCulling;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void cleanUp() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for (int i = 0; i < numMeshes; i++) {
            this.meshes[i].cleanUp();
        }
    }

}
