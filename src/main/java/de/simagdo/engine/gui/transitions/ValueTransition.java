package de.simagdo.engine.gui.transitions;

import de.simagdo.engine.toolbox.valueDrivers.ValueDriver;

public interface ValueTransition {

    public float getHiddenValue();

    public ValueDriver initDriver(float baseValue, float currentValue, boolean reverse, float delay, float totalDuration);

    public float getDuration();

}
