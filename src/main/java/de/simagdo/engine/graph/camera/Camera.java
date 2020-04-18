package de.simagdo.engine.graph.camera;

import de.simagdo.engine.inputsOutputs.userInput.Keyboard;
import de.simagdo.engine.inputsOutputs.windowing.WindowSizeListener;
import de.simagdo.engine.toolbox.misc.Listener;
import de.simagdo.engine.toolbox.misc.SmoothFloat;
import de.simagdo.engine.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * In-Game Camera. Keeping the Projection-View-Matrix updated. Also it allows user to alter the Pitch and the Yaw.
 */
public class Camera implements ICamera {

    public static final float PITCH_SENSITIVITY = 150f;
    public static final float YAW_SENSITIVITY = 190f;
    public static final float MAX_PITCH = 90;
    public static final float FOV = 50;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 1000;
    public static final float Y_OFFSET = 0.02f;
    public static final int SPEED = 3;
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Vector3f position = new Vector3f(50, 0, 0);
    private final Vector3f target = new Vector3f(128, Y_OFFSET, 128);
    private final ICameraControls controls;
    private final Window window;
    private float yaw = 0;
    private SmoothFloat pitch = new SmoothFloat(10, 10);
    private final SmoothFloat angleAroundPalyer = new SmoothFloat(0, 10);
    private final SmoothFloat distanceFromPlayer = new SmoothFloat(10, 5);
    private final List<Listener> moveListeners = new ArrayList<>();
    private final SmoothFloat forwardSpeed = new SmoothFloat(0, 10);
    private final SmoothFloat sideSpeed = new SmoothFloat(0, 10);

    public Camera(ICameraControls controls, Window window) {
        this.controls = controls;
        this.window = window;

        this.calculateProjectionMatrix();

        window.addSizeChangeListener((width, height) -> calculateProjectionMatrix());

    }

    @Override
    public void move(float delta) {
        this.calculatePitch(delta);
        this.calculateAngleAroundPlayer(delta);
        this.calculateZoom(delta);
        this.moveTarget(delta);
        float verticalDistance = this.calculateVerticalDistance();
        float horizontalDistance = this.calculateHorizontalChange();
        this.calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 360 - this.angleAroundPalyer.getAcutal();
        this.yaw %= 360;
        this.updateViewMatrix();
        this.notifyListeners();
    }

    @Override
    public Vector3f getPosition() {
        return this.position;
    }

    @Override
    public Matrix4f getViewMatrix() {
        return this.viewMatrix;
    }

    @Override
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    @Override
    public Matrix4f getProjectionViewMatrix() {
        return this.projectionMatrix.mulAffine(this.viewMatrix, null);
    }

    public Vector3f getTarget() {
        return target;
    }

    @Override
    public void addMoveListener(Listener listener) {
        this.moveListeners.add(listener);
    }

    private void updateViewMatrix() {
        this.viewMatrix.identity();
        this.viewMatrix.rotate((float) Math.toRadians(this.pitch.getAcutal()), new Vector3f(1, 0, 0));
        this.viewMatrix.rotate((float) Math.toRadians(this.yaw), new Vector3f(0, 1, 0));
        Vector3f negativeCameraPos = new Vector3f(-this.position.x, -this.position.y, -this.position.z);
        this.viewMatrix.translate(negativeCameraPos, this.viewMatrix);
    }

    private void calculateProjectionMatrix() {
        this.projectionMatrix.identity();
        float aspectRatio = this.window.getAspectRatio();
        float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float xScale = yScale / aspectRatio;
        float frustumLength = FAR_PLANE - NEAR_PLANE;

        this.projectionMatrix.m00(xScale);
        this.projectionMatrix.m11(yScale);
        this.projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustumLength));
        this.projectionMatrix.m23(-1);
        this.projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustumLength));
        this.projectionMatrix.m33(0);
    }

    private void calculatePitch(float delta) {
        float pitchChange = this.controls.getPitchInput() * PITCH_SENSITIVITY;
        this.pitch.increaseTarget(pitchChange);
        this.clampPitch();
        this.pitch.update(delta);
    }

    private void calculateZoom(float delta) {
        float targetZoom = this.distanceFromPlayer.getTarget();
        float zoomLevel = this.controls.getZoomInput() * 0.08f * targetZoom;
        targetZoom += zoomLevel;
        if (targetZoom < 1) targetZoom = 1;
        this.distanceFromPlayer.setTarget(targetZoom);
        this.distanceFromPlayer.update(delta);
    }

    private void moveTarget(float delta) {
        this.dealWithInputs();
        this.updateTargetPosition(delta);
    }

    private void dealWithInputs() {
        if (this.controls.goForwards()) this.forwardSpeed.setTarget(-10);
        else if (this.controls.goBackwards()) this.forwardSpeed.setTarget(10);
        else this.forwardSpeed.setTarget(0);

        if (this.controls.goRight()) this.sideSpeed.setTarget(10);
        else if (this.controls.goLeft()) this.sideSpeed.setTarget(-10);
        else this.sideSpeed.setTarget(0);
    }

    private void updateTargetPosition(float delta) {
        this.forwardSpeed.update(delta);
        this.sideSpeed.update(delta);
        this.target.x += this.forwardSpeed.getAcutal() * delta * SPEED * -Math.sin(Math.toRadians(this.yaw));
        this.target.z += this.forwardSpeed.getAcutal() * delta * SPEED * Math.cos(Math.toRadians(this.yaw));
        this.target.x += this.sideSpeed.getAcutal() * delta * SPEED * Math.sin(Math.toRadians(this.yaw + 90));
        this.target.z += this.sideSpeed.getAcutal() * delta * SPEED * -Math.cos(Math.toRadians(this.yaw + 90));
    }

    private void clampPitch() {
        if (this.pitch.getTarget() < 0) this.pitch.setTarget(0);
        else if (this.pitch.getTarget() > MAX_PITCH) this.pitch.setTarget(MAX_PITCH);
    }

    private void calculateAngleAroundPlayer(float delta) {
        float angleChange = this.controls.getYawInput() * YAW_SENSITIVITY;
        this.angleAroundPalyer.increaseTarget(-angleChange);
        this.angleAroundPalyer.update(delta);
    }

    private float calculateHorizontalChange() {
        return (float) (this.distanceFromPlayer.getAcutal() * Math.cos(Math.toRadians(this.pitch.getAcutal())));
    }

    private float calculateVerticalDistance() {
        return (float) (this.distanceFromPlayer.getAcutal() * Math.sin(Math.toRadians(this.pitch.getAcutal())));
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = this.angleAroundPalyer.getAcutal();
        this.position.x = this.target.x + (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        this.position.y = this.target.y + verticalDistance;
        this.position.z = this.target.z + (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
    }

    private void notifyListeners() {
        for (Listener listener : this.moveListeners) listener.eventOccurred();
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public SmoothFloat getPitch() {
        return pitch;
    }

    public void setPitch(SmoothFloat pitch) {
        this.pitch = pitch;
    }
}