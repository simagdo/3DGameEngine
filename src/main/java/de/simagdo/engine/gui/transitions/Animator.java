package de.simagdo.engine.gui.transitions;

import de.simagdo.engine.gui.uiComponent.UIComponent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Animator {

    private final UIComponent component;
    private final Map<Transition, Modifier> modifiers = new HashMap<>();
    private boolean posChange = false;
    private boolean scaleChange = false;
    private boolean alphaChange = false;
    private float xValue, yValue, width, height, alpha;

    public Animator(UIComponent component) {
        this.component = component;
        this.resetValues();
    }

    public void applyModifier(Transition transition, boolean reverse, float delay) {
        Modifier oldModifier = this.modifiers.remove(transition);
        Modifier newModifier = transition.createModifier(oldModifier, reverse, delay);
        this.modifiers.put(transition, newModifier);
    }

    public boolean isDoingTransition(Transition transition) {
        Modifier modifier = this.modifiers.get(transition);
        if (modifier == null) return false;
        return !modifier.hasFinishedTransition();
    }

    public void update(float delta) {
        if (this.modifiers.isEmpty()) return;

        this.resetValues();

        Iterator<Map.Entry<Transition, Modifier>> iterator = this.modifiers.entrySet().iterator();
        while (iterator.hasNext()) this.updateNextTransition(iterator, delta);

        this.updateComponentAsNecessary();

    }

    private void updateComponentAsNecessary() {
        if (this.posChange) this.component.notifyDimensionChange(scaleChange);
        if (alphaChange) this.component.updateTotalAlpha();
    }

    private void updateNextTransition(Iterator<Map.Entry<Transition, Modifier>> iterator, float delta) {
        Modifier modifier = iterator.next().getValue();
        modifier.update(delta, this);
        if (modifier.isRedundant()) iterator.remove();
    }

    private void resetValues() {
        this.xValue = 0;
        this.yValue = 0;
        this.width = 1;
        this.height = 1;
        this.alpha = 1;
        this.posChange = false;
        this.scaleChange = false;
        this.alphaChange = false;
    }

    public float getxValue() {
        return xValue;
    }

    public void applyX(float xValue, boolean change) {
        this.xValue += xValue;
        this.posChange |= change;
    }

    public float getyValue() {
        return yValue;
    }

    public void applyY(float yValue, boolean change) {
        this.yValue += yValue;
        this.posChange |= change;
    }

    public float getWidth() {
        return width;
    }

    public void applyWidth(float width, boolean change) {
        this.width *= width;
        this.scaleChange |= change;
        this.posChange |= change;
    }

    public float getHeight() {
        return height;
    }

    public void applyHeight(float height, boolean change) {
        this.height *= height;
        this.scaleChange |= change;
        this.posChange |= change;
    }

    public float getAlpha() {
        return alpha;
    }

    public void applyAlpha(float alpha, boolean change) {
        this.alpha *= alpha;
        this.alphaChange |= change;
    }

}
