package de.simagdo.engine.gui.constraints;

import de.simagdo.engine.gui.uiComponent.UIConstraint;
import de.simagdo.engine.gui.uiComponent.UIConstraints;

public class RatioConstraint extends UIConstraint {

    private UIConstraint otherConstraint;
    private final float aspectRatio;

    public RatioConstraint(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    @Override
    protected void completeSetUp(UIConstraints otherConstraints) {
        if (this.isPosValue())
            this.otherConstraint = this.isXAxis() ? otherConstraints.getYConstraint() : otherConstraints.getXConstraint();
        else
            this.otherConstraint = this.isXAxis() ? otherConstraints.getHeightConstraint() : otherConstraints.getWidthConstraint();
    }

    @Override
    public float getRelativeValue() {
        float otherRelativeValue = this.otherConstraint.getRelativeValue();
        float relativeValue = this.isXAxis() ? this.getParent().getRelativeWidthCoords(otherRelativeValue) : this.getParent().getRelativeHeightCoords(otherRelativeValue);
        return relativeValue * this.aspectRatio;
    }

    @Override
    public void setPixelValue(int pixels) {
        System.err.println("Can't set a pixel position value for an aspect ratio controlled UI constraint!");
    }

    @Override
    public void setRelativeValue(float value) {
        System.err.println("Can't set a relative position value for an aspect ratio controlled UI constraint!");
    }
}
