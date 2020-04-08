package de.simagdo.engine.toolbox.misc;

public class SmoothFloat {

    private final float agility;
    private float target;
    private float acutal;

    public SmoothFloat(float initialValue, float agility) {
        this.target = initialValue;
        this.acutal = initialValue;
        this.agility = agility;
    }

    public void update(float delta) {
        float offset = this.target - this.acutal;
        float change = offset * delta * this.agility;
        this.acutal += change;
    }

    public void increaseTarget(float increase) {
        this.target += increase;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public float getAcutal() {
        return this.acutal;
    }

    public float getTarget() {
        return this.target;
    }
}
