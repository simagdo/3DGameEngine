package de.simagdo.engine.gui.transitions;

import de.simagdo.engine.toolbox.valueDrivers.SliderDriver;
import de.simagdo.engine.toolbox.valueDrivers.ValueDriver;

public class SlideTransition implements ValueTransition {

    private final float offsetValue;
    private final float duration;
    private final float selfDelay;

    public SlideTransition(float offset, float duration) {
        this.offsetValue = offset;
        this.duration = duration;
        this.selfDelay = 0;
    }

    public SlideTransition(float offset, float duration, float selfDelay) {
        this.offsetValue = offset;
        this.duration = duration;
        this.selfDelay = selfDelay;
    }

    @Override
    public float getHiddenValue() {
        return this.offsetValue;
    }

    @Override
    public ValueDriver initDriver(float baseValue, float currentValue, boolean reverse, float delay, float totalDuration) {
        float target = reverse ? baseValue : this.offsetValue;
        float selfDelay = reverse ? totalDuration - (this.selfDelay + this.duration) : this.selfDelay;
        return new SliderDriver(currentValue, target, this.duration, delay + this.selfDelay);
    }

    @Override
    public float getDuration() {
        return this.duration + this.selfDelay;
    }
}
