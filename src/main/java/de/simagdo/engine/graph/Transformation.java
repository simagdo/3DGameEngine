package de.simagdo.engine.graph;

import de.simagdo.engine.items.GameItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    private final Matrix4f projectionMatrix;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f viewMatrix;
    private final Matrix4f orthoMatrix;
    private final Matrix4f modelMatrix;

    public Transformation() {
        this.projectionMatrix = new Matrix4f();
        this.modelViewMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        this.orthoMatrix = new Matrix4f();
        this.modelMatrix = new Matrix4f();
    }

    public Matrix4f updateProjectionMatrix() {
        return projectionMatrix;
    }

    public final Matrix4f updateProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public Matrix4f updateViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f updateViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }

    public Matrix4f getOrtoProjModelMatrix(GameItem gameItem, Matrix4f orthoMatrix) {
        Vector3f rotation = gameItem.getRotation();
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.identity().translate(gameItem.getPosition())
                .rotateX((float) Math.toRadians(-rotation.x))
                .rotateY((float) Math.toRadians(-rotation.y))
                .rotateZ((float) Math.toRadians(-rotation.z))
                .scale(gameItem.getScale());
        Matrix4f orthoMatrixCurr = new Matrix4f(orthoMatrix);
        orthoMatrixCurr.mul(modelMatrix);
        return orthoMatrixCurr;
    }

    public final Matrix4f getOrthoProjectionMatrix(float left, float right, float bottom, float top) {
        this.orthoMatrix.identity();
        this.orthoMatrix.setOrtho2D(left, right, top, bottom);
        return this.orthoMatrix;
    }

    public Matrix4f buildModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
        Vector3f rotation = gameItem.getRotation();
        this.modelMatrix.identity().translate(gameItem.getPosition())
                .rotateX((float) Math.toRadians(-rotation.x))
                .rotateY((float) Math.toRadians(-rotation.y))
                .rotateZ((float) Math.toRadians(-rotation.z))
                .scale(gameItem.getScale());
        this.modelViewMatrix.set(this.modelMatrix);
        return this.modelViewMatrix.mul(this.modelMatrix);
    }

}