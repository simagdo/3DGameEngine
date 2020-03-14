package de.simagdo.engine.gui.constraints;

import de.simagdo.engine.gui.uiComponent.UIConstraint;
import de.simagdo.engine.gui.uiComponent.UIConstraints;

public class CenterConstraint extends UIConstraint {

    private UIConstraint sizeConstraint;

    @Override
    protected void completeSetUp(UIConstraints otherConstraints) {
        this.sizeConstraint = this.isXAxis() ? otherConstraints.getWidthConstraint() : otherConstraints.getHeightConstraint();
    }

    @Override
    public float getRelativeValue() {
        float relativeSize = this.sizeConstraint.getRelativeValue();
        return (1 - relativeSize) / 2f;
    }

    @Override
    public void setPixelValue(int pixels) {
        System.err.println("Can't set a Pixel Position value for a centered UI Component!");
    }

    @Override
    public void setRelativeValue(float value) {
        System.err.println("Can't set a Pixel Position value for a centered UI Component!");
    }
}
