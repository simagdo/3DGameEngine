package de.simagdo.engine.graph.lights;

import org.joml.Vector3f;

public class DirectionalLight {

    private Vector3f color;
    private Vector3f direction;
    private float intensity;
    private OrthoCoords orthoCoords;
    private float shadowPosMult;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
        this.orthoCoords = new OrthoCoords();
        this.shadowPosMult = 1;
    }

    public DirectionalLight(DirectionalLight light) {
        this(new Vector3f(light.getColor()), new Vector3f(light.getDirection()), light.getIntensity());
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public OrthoCoords getOrthoCoords() {
        return this.orthoCoords;
    }

    public void setOrthoCoords(float left, float right, float bottom, float top, float near, float far) {
        this.orthoCoords.left = left;
        this.orthoCoords.right = right;
        this.orthoCoords.bottom = bottom;
        this.orthoCoords.top = top;
        this.orthoCoords.near = near;
        this.orthoCoords.far = far;
    }

    public float getShadowPosMult() {
        return this.shadowPosMult;
    }

    public void setShadowPosMult(float shadowPosMult) {
        this.shadowPosMult = shadowPosMult;
    }
}