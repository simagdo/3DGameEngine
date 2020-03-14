package de.simagdo.engine.gui.uiComponent;

public class UIConstraints {

    private UIConstraint xConstraint;
    private UIConstraint yConstraint;
    private UIConstraint widthConstraint;
    private UIConstraint heightConstraint;

    public UIConstraints() {

    }

    public UIConstraints(UIConstraint x, UIConstraint y, UIConstraint width, UIConstraint height) {
        this.setXConstraint(x);
        this.setYConstraint(y);
        this.setWidthConstraint(width);
        this.setHeightConstraint(height);
    }

    public void notifyAdded(UIComponent current, UIComponent parent) {
        try {
            this.xConstraint.notifyAdded(this, current, parent);
            this.yConstraint.notifyAdded(this, current, parent);
            this.widthConstraint.notifyAdded(this, current, parent);
            this.heightConstraint.notifyAdded(this, current, parent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UIConstraint getXConstraint() {
        return xConstraint;
    }

    public void setXConstraint(UIConstraint xConstraint) {
        this.xConstraint = xConstraint;
        this.xConstraint.setxAxis(true, true);
    }

    public UIConstraint getYConstraint() {
        return yConstraint;
    }

    public void setYConstraint(UIConstraint yConstraint) {
        this.yConstraint = yConstraint;
        this.yConstraint.setxAxis(false, true);
    }

    public UIConstraint getWidthConstraint() {
        return widthConstraint;
    }

    public void setWidthConstraint(UIConstraint widthConstraint) {
        this.widthConstraint = widthConstraint;
        this.widthConstraint.setxAxis(true, false);
    }

    public UIConstraint getHeightConstraint() {
        return heightConstraint;
    }

    public void setHeightConstraint(UIConstraint heightConstraint) {
        this.heightConstraint = heightConstraint;
        this.heightConstraint.setxAxis(false, false);
    }
}
