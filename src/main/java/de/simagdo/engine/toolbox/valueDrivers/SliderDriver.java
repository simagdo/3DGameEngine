package de.simagdo.engine.toolbox.valueDrivers;

import de.simagdo.engine.toolbox.mathUtils.Maths;

public class SliderDriver extends ValueDriver {

    private final float startValue;
    private final float endValue;

    public SliderDriver(float start, float end, float length) {
        super(length);
        this.startValue = start;
        this.endValue = end;
    }

    public SliderDriver(float start, float end, float length, float timeDelay) {
        super(timeDelay, length);
        this.startValue = start;
        this.endValue = end;
    }

    @Override
    protected float calculateValue(float time) {
        if (super.hasCompletedOnePeriod()) return this.endValue;
        return Maths.cosInterpolate(this.startValue, this.endValue, time);
    }
}
