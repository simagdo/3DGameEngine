package de.simagdo.engine.gui.constraints;

import de.simagdo.engine.gui.uiComponent.UIConstraint;
import de.simagdo.engine.gui.uiComponent.UIConstraints;

public class RelativeConstraint extends UIConstraint {

    private float relativeValue;

    public RelativeConstraint(float value) {
        this.relativeValue = value;
    }

    @Override
    protected void completeSetUp(UIConstraints otherConstraints) {

    }

    @Override
    public float getRelativeValue() {
        return this.relativeValue;
    }

    @Override
    public void setPixelValue(int pixels) {
        this.relativeValue = pixels / super.getParentPixelSize();
        this.notifyDimensionChange(!super.isPosValue());
    }

    @Override
    public void setRelativeValue(float value) {
        if (this.relativeValue == value) return;

        this.relativeValue = value;
        this.notifyDimensionChange(!super.isPosValue());
    }
}
