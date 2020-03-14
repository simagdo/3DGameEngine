package de.simagdo.engine.gui.uiComponent;

public abstract class UIConstraint {

    private boolean xAxis;
    private boolean posValue;
    private UIComponent current;
    private UIComponent parent;

    protected void setxAxis(boolean xAxis, boolean posValue) {
        this.xAxis = xAxis;
        this.posValue = posValue;
    }

    public boolean isXAxis() {
        return xAxis;
    }

    public boolean isPosValue() {
        return posValue;
    }

    public UIComponent getCurrent() {
        return current;
    }

    public UIComponent getParent() {
        return parent;
    }

    protected float getParentPixelSize() {
        return this.xAxis ? this.parent.getPixelWidth() : this.parent.getPixelHeight();
    }

    protected void notifyDimensionChange(boolean scaleChange) {
        this.current.notifyDimensionChange(scaleChange);
    }

    protected void notifyAdded(UIConstraints otherConstraints, UIComponent current, UIComponent parent) {
        this.current = current;
        this.parent = parent;
        this.completeSetUp(otherConstraints);
    }

    protected abstract void completeSetUp(UIConstraints otherConstraints);

    public abstract float getRelativeValue();

    public abstract void setPixelValue(int pixels);

    public abstract void setRelativeValue(float value);

}
