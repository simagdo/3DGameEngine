package de.simagdo.engine.toolbox.valueDrivers;

public abstract class ValueDriver {

    private final float startTime;
    private final float length;
    private float currentTime = 0;
    private float currentValue;
    private boolean firstPeriodDone = false;

    public ValueDriver(float length) {
        this.length = length;
        this.startTime = 0;
    }

    public ValueDriver(float startTime, float length) {
        this.length = length;
        this.startTime = startTime;
    }

    public float update(float delta) {
        this.currentTime += delta;

        if (this.currentTime < this.startTime) {
            this.currentTime = this.calculateValue(0);
            return this.currentValue;
        }
        float totalTime = this.length + this.startTime;
        if (this.currentTime >= totalTime) {
            this.currentTime %= totalTime;
            this.firstPeriodDone = true;
        }

        float relativeTime = (this.currentTime - this.startTime) / this.length;
        this.currentTime = this.calculateValue(relativeTime);
        return this.currentTime;
    }

    /**
     * @param time - A Value between 0 and 1, indicating how far into this period.
     * @return The Value at this Time.
     */
    protected abstract float calculateValue(float time);

    public boolean hasCompletedOnePeriod() {
        return this.firstPeriodDone;
    }

    public float getCurrentValue() {
        return this.currentValue;
    }

}
