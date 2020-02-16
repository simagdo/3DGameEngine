package de.simagdo.engine.graph.weather;

import org.joml.Vector3f;

public class Fog {

    private boolean active;
    private Vector3f colour;
    private float density;
    public static Fog NOFOG = new Fog();

    public Fog() {
        this.active = false;
        this.colour = new Vector3f();
        this.density = 0;
    }

    public Fog(boolean active, Vector3f colour, float density) {
        this.active = active;
        this.colour = colour;
        this.density = density;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public static Fog getNOFOG() {
        return NOFOG;
    }

    public static void setNOFOG(Fog NOFOG) {
        Fog.NOFOG = NOFOG;
    }
}