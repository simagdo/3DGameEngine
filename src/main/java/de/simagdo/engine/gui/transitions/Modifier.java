package de.simagdo.engine.gui.transitions;

import de.simagdo.engine.toolbox.valueDrivers.ValueDriver;

import java.util.Map;

public class Modifier {

    private final Map<TransitionType, ValueDriver> valueDrivers;
    private final float duration;
    private final boolean reverse;
    private float time = 0;

    protected Modifier(Map<TransitionType, ValueDriver> valueDrivers, boolean reverse, float duration) {
        this.valueDrivers = valueDrivers;
        this.duration = duration;
        this.reverse = reverse;
    }

    protected void update(float delta, Animator animator) {
        boolean valueChanging = !this.hasFinishedTransition();
        this.time += delta;
        this.valueDrivers.forEach((key, value1) -> {
            float value = value1.update(delta);
            key.applyValue(animator, value, valueChanging);
        });
    }

    protected boolean hasFinishedTransition() {
        return this.time > this.duration;
    }

    protected float getCurrentValue(TransitionType type) {
        ValueDriver driver = this.valueDrivers.get(type);
        return driver.getCurrentValue();
    }

    protected boolean isRedundant() {
        return this.reverse && this.hasFinishedTransition();
    }

}
