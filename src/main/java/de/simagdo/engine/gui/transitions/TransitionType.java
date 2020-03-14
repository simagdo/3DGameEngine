package de.simagdo.engine.gui.transitions;

public enum TransitionType {

    X_POS(0, Animator::applyX),
    Y_POS(0, Animator::applyY),
    WIDTH(1, Animator::applyWidth),
    HEIGHT(1, Animator::applyHeight),
    ALPHA(1, Animator::applyAlpha);

    private final float baseValue;
    private final ValueSetter setter;

    private TransitionType(float normalValue, ValueSetter setter) {
        this.baseValue = normalValue;
        this.setter = setter;
    }

    public float getBaseValue() {
        return baseValue;
    }

    public void applyValue(Animator animator, float value, boolean change) {
        this.setter.setValue(animator, value, change);
    }

    private static interface ValueSetter {
        public void setValue(Animator animator, float value, boolean change);
    }

}
