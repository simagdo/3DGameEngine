package de.simagdo.engine.graph.animation;

import org.joml.Matrix4f;

import java.util.Arrays;

public class AnimatedFrame {

    public static final int MAX_JOINTS = 150;
    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
    private final Matrix4f[] localJointMatrices;
    private final Matrix4f[] jointMatrices;

    public AnimatedFrame() {
        this.localJointMatrices = new Matrix4f[MAX_JOINTS];
        Arrays.fill(this.localJointMatrices, IDENTITY_MATRIX);

        this.jointMatrices = new Matrix4f[MAX_JOINTS];
        Arrays.fill(this.jointMatrices, IDENTITY_MATRIX);
    }

    public Matrix4f[] getLocalJointMatrices() {
        return this.localJointMatrices;
    }

    public Matrix4f[] getJointMatrices() {
        return this.jointMatrices;
    }

    public void setMatrix(int pos, Matrix4f jointMatrix) {
        this.jointMatrices[pos] = jointMatrix;
    }

}
