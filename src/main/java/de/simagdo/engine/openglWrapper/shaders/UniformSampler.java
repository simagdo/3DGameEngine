package de.simagdo.engine.openglWrapper.shaders;

import org.lwjgl.opengl.GL20;

public class UniformSampler extends Uniform {

    private int currentValue;
    private boolean used = false;

    public UniformSampler(String name) {
        super(name);
    }

    public void loadTexUnit(int texUnit) {
        if (this.used && this.currentValue == texUnit) {
            return;
        }

        this.currentValue = texUnit;
        this.used = true;

        GL20.glUniform1f(super.getLocation(), texUnit);
    }

}
