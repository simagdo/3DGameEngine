package de.simagdo.engine;

import de.simagdo.engine.toolbox.mathUtils.RollingAverage;
import org.lwjgl.glfw.GLFW;

public class Timer {

    private static final float MAX_DELTA = 0.1f;
    private static final float STABLE_TIME = 1.5f;
    private static final int ROLL_AVG_COUNT = 20;
    private static final int NANOS_IN_SECOND = 1000 * 1000 * 1000;
    private final float idealDelta;
    private RollingAverage deltaAverage = new RollingAverage(ROLL_AVG_COUNT);
    private long lastFrameTime;
    private float applicationTime = 0;
    private float delta;

    public Timer(float idealFPS) {
        this.idealDelta = 1f / idealFPS;
        this.delta = idealDelta;
        this.lastFrameTime = this.getCurrentTime();
    }

    public void update() {
        long currentFrameTime = this.getCurrentTime();
        float frameLength = Math.min(MAX_DELTA, (float) (currentFrameTime - this.lastFrameTime) / NANOS_IN_SECOND);
        this.lastFrameTime = currentFrameTime;
        this.applicationTime += frameLength;
        if (this.applicationTime < STABLE_TIME) frameLength = this.idealDelta;

        this.deltaAverage.addValues(frameLength);
        this.delta = this.deltaAverage.calculate();

    }

    private long getCurrentTime() {
        return (long) (GLFW.glfwGetTime() * NANOS_IN_SECOND);
    }

    public float getDelta() {
        return this.delta;
    }

}
