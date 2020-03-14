package de.simagdo.engine.gui.uiComponent;

import de.simagdo.engine.gui.transitions.Animator;
import de.simagdo.engine.gui.transitions.Transition;
import de.simagdo.engine.inputsOutputs.userInput.MouseButton;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public abstract class UIComponent {

    protected boolean visible = true;
    private List<UIComponent> children = new ArrayList<>();
    private List<UIComponent> childrenToAdd = new ArrayList<>();
    private List<UIComponent> childrenToRemove = new ArrayList<>();
    private UIComponent parent;
    private UIConstraints constraints;
    private float xModifier, yModifier;
    private float widthModifier = 1, heightModifier = 1;
    private Animator animator = null;

    //Screen Coords
    private float x, y, widht, height;
    private boolean initialized = false;
    private boolean reloadOnSizeChange = false;
    private float totalAlpha = 1;
    private float selfAlpha = 1;
    private boolean removeFlag = false;
    private boolean displayed = true;
    private int level = 0;
    private Transition displayTransition;
    private float displayDelay;
    private float hideDelay;

    public void show(boolean show) {
        this.visible = show;
    }

    public boolean isShown() {
        return this.visible;
    }

    public void add(UIComponent component, UIConstraints constraints) {
        component.constraints = constraints;
        constraints.notifyAdded(component, this);
        component.parent = this;
        if (initialized) this.initChild(component);
        else this.childrenToAdd.add(component);
    }

    public void addInStyle(UIComponent component, UIConstraints constraints) {
        component.setHidden();
        this.add(component, constraints);
        component.display(true);
    }

    public void removeInStyle() {
        if (this.displayTransition == null) this.remove();
        else {
            this.removeFlag = true;
            this.display(false);
        }
    }

    public void setHidden() {
        this.displayed = false;
        this.show(false);
    }

    public void setDisplayed() {
        this.displayed = true;
        this.show(true);
    }

    public void display(boolean display) {
        if (display == this.displayed) return;

        this.displayed = display;

        if (display) this.show(true);

        this.doDisplayAnimation(display, 0, true);
    }

    private void doDisplayAnimation(boolean display, float parentDelay, boolean head) {
        if (!isShown() || (!this.displayed && !head)) return;

        float totalDelay = parentDelay + (display ? displayDelay : hideDelay);
        if (displayTransition != null) this.getAnimator().applyModifier(displayTransition, display, totalDelay);

        children.forEach(component -> component.doDisplayAnimation(display, totalDelay, false));

    }

    public boolean hideAnimationFinished() {
        if (this.visible && this.displayTransition != null && this.animator.isDoingTransition(this.displayTransition))
            return false;

        for (UIComponent child : children) if (!child.hideAnimationFinished()) return false;

        return true;
    }

    public void setDisplayAnimation(Transition displayTransition, float displayDelay, float hideDelay) {
        this.displayTransition = displayTransition;
        this.displayDelay = displayDelay;
        this.hideDelay = hideDelay;
    }

    public void setDisplayAnimation(Transition displayTransition) {
        this.displayTransition = displayTransition;
        this.displayDelay = 0;
        this.hideDelay = 0;
    }

    //TODO
    public void addText() {

    }

    public float getAbsX() {
        return this.x;
    }

    public float getAbsY() {
        return this.y;
    }

    public float getAbsWidth() {
        return this.widht;
    }

    public float getAbsHeight() {
        return this.height;
    }

    public UIConstraint getXConstraint() {
        return this.constraints.getXConstraint();
    }

    public UIConstraint getYConstraint() {
        return this.constraints.getYConstraint();
    }

    public UIConstraint getWidthConstraint() {
        return this.constraints.getWidthConstraint();
    }

    public UIConstraint getHeightConstraint() {
        return this.constraints.getHeightConstraint();
    }

    public float getRelativeWidthCoords(float relativeHeight) {
        return relativeHeight / this.getAbsAspectRatio();
    }

    public float getRelativeHeightCoords(float relativeWidth) {
        return relativeWidth * this.getAbsAspectRatio();
    }

    public float getAbsAspectRatio() {
        return this.getPixelWidth() / this.getPixelHeight();
    }

    //TODO
    public float getPixelWidth() {
        return this.widht;
    }

    //TODO
    public float getPixelHeight() {
        return this.height;
    }

    //TODO
    public boolean isClickedOn(MouseButton button) {
        return false;
    }

    public Animator getAnimator() {
        if (animator == null) this.animator = new Animator(this);
        return this.animator;
    }

    //TODO
    public Vector2f getMouseRelativePos() {
        return new Vector2f(0, 0);
    }

    public void setAlpha(float selfAlpha) {
        this.selfAlpha = selfAlpha;
        if (this.initialized) this.updateTotalAlpha();
    }

    public void remove() {
        this.parent.childrenToRemove.add(this);
    }

    /**
     * Get the Alpha Value.
     *
     * @return The Alpha Setting for this Component only. This is not the total alpha for this component,
     * which is affected by the alpha value of parent as well.
     */
    public float getSelfAlpha() {
        return this.selfAlpha;
    }

    /**
     * @return The actual alpha Value that this UI Element will be rendered at.
     */
    public float getTotalAlpha() {
        return this.totalAlpha;
    }

    public void updateTotalAlpha() {
        this.calculateTotalAlpha();
        this.children.forEach(UIComponent::updateTotalAlpha);
    }

    private void removeOldComponents() {
        int index = 0;
        while (index < this.childrenToRemove.size()) {
            UIComponent component = this.childrenToRemove.get(index++);
            this.children.remove(component);
            component.delete();
        }
        this.childrenToRemove.clear();
    }

    private void calculateTotalAlpha() {
        float animationAlpha = this.animator == null ? 1 : this.animator.getAlpha();
        this.totalAlpha = this.parent.totalAlpha * this.selfAlpha * animationAlpha;
    }

    protected void forceInitialization(float absX, float absY, float absWidth, float absHeight) {
        this.x = absX;
        this.y = absY;
        this.widht = absWidth;
        this.height = absHeight;
        this.initialized = true;
    }

    protected abstract void init();

    protected void getRenderData(UIRenderBundle renderData) {
        //usually empty. Override in Sub Class if providing some render capability.
    }

    /**
     * Gets called when a component is removed.
     * Basically just needed to delete TExt VAOs from Memory.
     */
    public void delete() {
        this.children.forEach(UIComponent::delete);
        this.childrenToAdd.forEach(UIComponent::delete);
        this.childrenToRemove.forEach(UIComponent::delete);
    }

    public List<UIComponent> getChildren() {
        return this.children;
    }

    protected UIComponent getParent() {
        return this.parent;
    }

    public final void update(UIRenderBundle renderData, float delta) {
        if (!this.visible) return;

        this.updateSelf();
        this.updateAnimation(delta);
        this.getRenderData(renderData);
        int index = 0;
        while (index < this.children.size()) this.children.get(index++).update(renderData, delta);

    }

    private void updateAnimation(float delta) {
        if (this.animator != null) this.animator.update(delta);
        if (!this.displayed && this.hideAnimationFinished()) {
            this.show(false);
            if (this.removeFlag) this.remove();
        }
    }

    public void replace(UIComponent component) {
        this.remove();
        this.parent.add(component, this.constraints);
    }

    protected void calculateScreenSpacePosition(boolean calcScale) {
        this.x = this.parent.x + (this.constraints.getXConstraint().getRelativeValue() + this.xModifier + this.getAnimationX()) * this.parent.widht;
        this.y = this.parent.y + (this.constraints.getYConstraint().getRelativeValue() + this.yModifier + this.getAnimationY()) * this.parent.height;

        if (calcScale) {
            this.widht = this.constraints.getWidthConstraint().getRelativeValue() * this.parent.widht * this.widthModifier * this.getAnimationWidth();
            this.height = this.constraints.getHeightConstraint().getRelativeValue() * this.parent.height * this.heightModifier * this.getAnimationHeight();
        }
    }

    private float getAnimationWidth() {
        if (this.animator == null) return 1;
        return this.animator.getWidth();
    }

    private float getAnimationHeight() {
        if (this.animator == null) return 1;
        return this.animator.getHeight();
    }

    private float getAnimationX() {
        if (this.animator == null) return 0;
        return this.animator.getxValue() * this.constraints.getWidthConstraint().getRelativeValue();
    }

    private float getAnimationY() {
        if (this.animator == null) return 0;
        return this.animator.getyValue() * this.constraints.getHeightConstraint().getRelativeValue();
    }

    protected abstract void updateSelf();

    public void clear() {
        this.childrenToRemove.addAll(this.children);
    }

    public void clearInStyle() {
        this.children.forEach(UIComponent::removeInStyle);
    }

    public final void notifyDimensionChange(boolean scaleChange) {
        this.calculateScreenSpacePosition(scaleChange);
        this.updateContentsToDimensionChange(scaleChange);
    }

    protected void updateContentsToDimensionChange(boolean scaleChange) {
        if (scaleChange && this.reloadOnSizeChange) {
            this.reload();
            return;
        }
        this.children.forEach(component -> component.notifyDimensionChange(scaleChange));
    }

    public float absToRelativeX(float absX) {
        return (absX - this.x) / this.widht;
    }

    public float absToRelativeY(float absY) {
        return (absY - this.y) / this.height;
    }

    //TODO
    public boolean isMouseOver() {
        return false;
    }

    public float getxModifier() {
        return this.xModifier;
    }

    public float getyModifier() {
        return this.yModifier;
    }

    public float getWidthModifier() {
        return this.widthModifier;
    }

    public float getHeightModifier() {
        return this.heightModifier;
    }

    public void setModifyX(float x) {
        if (x == this.xModifier) return;

        this.xModifier = x;
        if (this.initialized) this.notifyDimensionChange(false);
    }

    public void setModifyY(float y) {
        if (y == this.yModifier) return;

        this.yModifier = y;
        if (this.initialized) this.notifyDimensionChange(false);
    }

    public void setModifyWidth(float width) {
        if (width == this.widthModifier) return;

        this.widthModifier = width;
        if (this.initialized) this.notifyDimensionChange(false);
    }

    public void setModifyHeight(float height) {
        if (height == this.heightModifier) return;

        this.heightModifier = height;
        if (this.initialized) this.notifyDimensionChange(false);
    }

    public void setLevel(int level) {
        this.level = level;
        for (UIComponent child : this.children) {
            if (level > child.level) child.setLevel(level);
        }
    }

    public int getLevel() {
        return this.level;
    }

    protected void reload() {
        this.clear();
        this.init();
    }

    private void initChild(UIComponent child) {
        this.children.add(child);
        child.level = Math.max(level, child.level);
        child.calculateScreenSpacePosition(true);
        child.init();
        child.calculateTotalAlpha();
        child.initialized = true;
        child.initAllChildren();
    }

    private void initAllChildren() {
        for (UIComponent child : this.children) {
            this.initChild(child);
        }

        this.childrenToAdd.clear();
    }

}
