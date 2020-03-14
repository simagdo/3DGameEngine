package de.simagdo.engine.gui.constraints;

import de.simagdo.engine.gui.uiComponent.UIConstraint;
import de.simagdo.engine.gui.uiComponent.UIConstraints;

public class PixelConstraint extends UIConstraint {

    private int value;
    private final boolean flipAxis;

    public PixelConstraint(int value) {
        this.value = value;
        this.flipAxis = false;
    }

    public PixelConstraint(int value, boolean flipAxis) {
        this.value = value;
        this.flipAxis = flipAxis;
    }

    @Override
    protected void completeSetUp(UIConstraints otherConstraints) {

    }

    @Override
    public float getRelativeValue() {
        float parentSizePixels = this.isXAxis() ? this.getParent().getPixelWidth() : this.getParent().getPixelHeight();
        float relativeValue = this.value / parentSizePixels;
        if (this.flipAxis) return 1 - relativeValue;
        return relativeValue;
    }

    @Override
    public void setPixelValue(int pixels) {
        if (this.value == pixels) return;

        this.value = pixels;
        this.notifyDimensionChange(!super.isPosValue());
    }

    @Override
    public void setRelativeValue(float value) {
        value = this.flipAxis ? 1 - value : value;
        this.value = Math.round(super.getParentPixelSize() * value);
        this.notifyDimensionChange(!super.isPosValue());
    }
}
