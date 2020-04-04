package de.simagdo.engine.graph.camera;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.entities.Player;
import de.simagdo.engine.graph.Transformation;
import de.simagdo.engine.inputsOutputs.userInput.Mouse;
import de.simagdo.engine.inputsOutputs.userInput.MouseButton;
import de.simagdo.game.gui.main.GameManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    private final Vector3f rotation;
    private Matrix4f viewMatrix;
    private float pitch = 10;
    private float yaw;
    private float roll;
    private float distanceFromPlayer = 50;
    private float angleAroundPalyer = 0;
    private Mouse mouse;
    private Player player;

    public Camera() {
        this.position = new Vector3f();
        rotation = new Vector3f();
        this.viewMatrix = new Matrix4f();
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Camera(Vector3f position, Vector3f rotation, GameEngine engine, Player player) {
        this.position = position;
        this.rotation = rotation;
        this.player = player;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public void setViewMatrix(Matrix4f viewMatrix) {
        this.viewMatrix = viewMatrix;
    }

    public Matrix4f updateViewMatrix() {
        return Transformation.updateGenericViewMatrix(this.position, this.rotation, this.viewMatrix);
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if (offsetZ != 0) {
            this.position.x += (float) Math.sin(Math.toRadians(this.rotation.y)) * -1.0f * offsetZ;
            this.position.z += (float) Math.cos(Math.toRadians(this.rotation.y)) * offsetZ;
        }
        if (offsetX != 0) {
            this.position.x += (float) Math.sin(Math.toRadians(this.rotation.y - 90)) * -1.0f * offsetX;
            this.position.z += (float) Math.cos(Math.toRadians(this.rotation.y - 90)) * offsetX;
        }
        this.position.y += offsetY;
    }

    public Vector3f getRotation() {
        return this.rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        this.rotation.x += offsetX;
        this.rotation.y += offsetY;
        this.rotation.z += offsetZ;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void move() {
        if (GameManager.isInitialized()) {
            this.calculateZoom();
            this.calculatePitch();
            this.calculateAngleAroundPlayer();
            float verticalDistance = this.calculateVerticalDistance();
            float horizontalDistance = this.calculateHorizontalChange();
            this.calculateCameraPosition(horizontalDistance, verticalDistance);
            this.yaw = 180 - (this.player.getPosition().y + this.angleAroundPalyer);
        }
    }

    private void calculateZoom() {
        float zoomLevel = GameManager.getMouse().getDx() * 0.3f;
        this.distanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch() {
        if (GameManager.getMouse().isButtonDown(MouseButton.LEFT)) {
            float pitchChange = GameManager.getMouse().getDy() * 0.1f;
            this.pitch -= pitchChange;
            System.out.println("Pitch: " + this.pitch);
        }
    }

    private void calculateAngleAroundPlayer() {
        if (GameManager.getMouse().isButtonDown(MouseButton.RIGHT)) {
            float angleChange = GameManager.getMouse().getDx() * 0.3f;
            this.angleAroundPalyer -= angleChange;
        }
    }

    private float calculateHorizontalChange() {
        return (float) (this.distanceFromPlayer * Math.cos(Math.toRadians(this.pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (this.distanceFromPlayer * Math.sin(Math.toRadians(this.pitch)));
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = this.player.getRotation().y + this.angleAroundPalyer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        this.position.x = this.player.getPosition().x - offsetX;
        this.position.y = this.player.getPosition().y + verticalDistance;
        this.position.z = this.player.getPosition().z - offsetZ;
        this.player.getGameItem().setPosition(this.position.x, this.position.y, this.position.z);
    }

}