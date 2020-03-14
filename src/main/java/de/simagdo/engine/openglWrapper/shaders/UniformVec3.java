package de.simagdo.engine.openglWrapper.shaders;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

public class UniformVec3 extends Uniform {

    private Vector3f current;
    private boolean used = false;

    public UniformVec3(String name) {
        super(name);
    }

    public void loadVec3(Vector3f vector) {
        this.loadVec3(vector.x, vector.y, vector.z);
    }

    public void loadVec3(float x, float y, float z) {
        if (used && (x == this.current.x && y == this.current.y && z == this.current.z)) {
            return;
        }

        this.used = true;
        this.current.x = x;
        this.current.y = y;
        this.current.z = z;

        GL20.glUniform3f(super.getLocation(), x, y, z);

    }

}
