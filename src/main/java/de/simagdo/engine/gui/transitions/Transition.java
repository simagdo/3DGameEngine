package de.simagdo.engine.gui.transitions;

import de.simagdo.engine.toolbox.valueDrivers.ValueDriver;

import java.util.HashMap;
import java.util.Map;

public class Transition {

    private final Map<TransitionType, ValueTransition> valueDrivers = new HashMap<>();
    private float duration = 0;

    public Transition add(TransitionType type, ValueTransition transition) {
        this.valueDrivers.put(type, transition);
        this.duration = Math.max(this.duration, transition.getDuration());
        return this;
    }

    protected Modifier createModifier(Modifier oldModifier, boolean reverse, float delay) {
        float transitionDuration = this.duration + delay;
        Map<TransitionType, ValueDriver> driverInstances = new HashMap<>();
        this.valueDrivers.forEach((type, transition) -> {
            ValueDriver driverInstance = this.initDriver(type, transition, oldModifier, reverse, delay);
            driverInstances.put(type, driverInstance);
        });
        return new Modifier(driverInstances, reverse, transitionDuration);
    }

    private ValueDriver initDriver(TransitionType type, ValueTransition transition, Modifier oldModifier, boolean reverse, float delay) {
        float baseValue = type.getBaseValue();
        float currentValue = reverse ? transition.getHiddenValue() : baseValue;
        if (oldModifier != null) currentValue = oldModifier.getCurrentValue(type);
        return transition.initDriver(baseValue, currentValue, reverse, delay, duration);
    }

}
