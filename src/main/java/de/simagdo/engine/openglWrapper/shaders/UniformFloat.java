package de.simagdo.engine.openglWrapper.shaders;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform {

    private float currentValue;
    private boolean used = false;

    public UniformFloat(String name) {
        super(name);
    }

    public void loadFloat(float value) {
        if (this.used && value == this.currentValue) {
            return;
        }

        this.currentValue = value;
        this.used = true;

        GL20.glUniform1f(super.getLocation(), value);

    }

}
