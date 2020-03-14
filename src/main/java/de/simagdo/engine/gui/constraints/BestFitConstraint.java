package de.simagdo.engine.gui.constraints;

import de.simagdo.engine.gui.uiComponent.UIComponent;
import de.simagdo.engine.gui.uiComponent.UIConstraint;
import de.simagdo.engine.gui.uiComponent.UIConstraints;

public class BestFitConstraint extends UIConstraint {

    private float aspectRatio;

    public BestFitConstraint(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    @Override
    protected void completeSetUp(UIConstraints otherConstraints) {

    }

    @Override
    public float getRelativeValue() {
        UIComponent parent = super.getParent();
        float otherLength = super.isXAxis() ? parent.getRelativeWidthCoords(1) : parent.getRelativeHeightCoords(1);
        otherLength = super.isXAxis() ? otherLength * this.aspectRatio : otherLength / this.aspectRatio;
        return Math.max(1, otherLength);
    }

    @Override
    public void setPixelValue(int pixels) {
        System.err.println("Can't set a Pixel scale value for a best fi UI Component!");
    }

    @Override
    public void setRelativeValue(float value) {
        System.err.println("Can't set a Pixel scale value for a best fi UI Component!");
    }
}
