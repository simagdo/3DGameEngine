package de.simagdo.engine.openglWrapper.shaders;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class Uniform {

    private static final int NOT_FOUND = -1;
    private final String name;
    private int location;

    protected Uniform(String name) {
        this.name = name;
    }

    protected void storeUniformLocation(int programID) {
        this.location = glGetUniformLocation(programID, this.name);
        if (this.location == NOT_FOUND) System.err.printf("No Uniform Variable called %s found!", this.name);
    }

    protected int getLocation() {
        return this.location;
    }

    public String getName() {
        return name;
    }
}
