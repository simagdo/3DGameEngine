package de.simagdo.engine.gui.constraints;

import de.simagdo.engine.gui.uiComponent.UIConstraint;
import de.simagdo.engine.gui.uiComponent.UIConstraints;

public class ConstraintFactory {

    public static UIConstraints getDefault() {
        return new UIConstraints();
    }

    public static UIConstraints getRelative(float x, float y, float widht, float height) {
        return new UIConstraints(new RelativeConstraint(x), new RelativeConstraint(y), new RelativeConstraint(widht), new RelativeConstraint(height));
    }

    public static UIConstraints getPixel(int x, int y, int width, int height) {
        return new UIConstraints(new PixelConstraint(x), new PixelConstraint(y), new PixelConstraint(width), new PixelConstraint(height));
    }

    public static UIConstraints getFill() {
        return getRelative(0, 0, 1, 1);
    }

    public static UIConstraints getBestFit(float aspectRatio) {
        UIConstraints constraints = new UIConstraints();
        constraints.setXConstraint(new CenterConstraint());
        constraints.setYConstraint(new CenterConstraint());
        constraints.setWidthConstraint(new BestFitConstraint(aspectRatio));
        constraints.setHeightConstraint(new BestFitConstraint(aspectRatio));
        return constraints;
    }

}
