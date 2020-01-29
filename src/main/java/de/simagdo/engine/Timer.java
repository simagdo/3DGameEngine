package de.simagdo.engine;

public class Timer {

    private double lastLoopTime;

    public void init() {
        this.lastLoopTime = this.getTime();
    }

    public double getTime() {
        return System.nanoTime() / 1000_000_000.0;
    }

    public float getElapsedTime() {
        double time = this.getTime();
        float elapsedTime = (float) (time - this.lastLoopTime);
        this.lastLoopTime = time;
        return elapsedTime;
    }

    public double getLastLoopTime() {
        return this.lastLoopTime;
    }
}
