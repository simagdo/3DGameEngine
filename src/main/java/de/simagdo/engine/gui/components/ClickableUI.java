package de.simagdo.engine.gui.components;

import de.simagdo.engine.gui.uiComponent.UIComponent;
import de.simagdo.engine.inputsOutputs.userInput.Mouse;
import de.simagdo.engine.inputsOutputs.userInput.MouseButton;
import de.simagdo.engine.gui.mainUI.UIMaster;

import java.util.ArrayList;
import java.util.List;

public abstract class ClickableUI extends UIComponent {

    private List<MouseListener> listeners = new ArrayList<>();
    private boolean blocked = false;
    private boolean mouseOver = false;

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void addMouseListener(MouseListener listener) {
        this.listeners.add(listener);
    }

    @Override
    protected void updateSelf() {
        if (this.blocked) return;

        this.chechMouseOver();
        if (this.mouseOver) this.checkClicks();
    }

    protected void mouseOver(boolean newMouseOverState) {
        this.mouseOver = newMouseOverState;
        this.fireListeners(new EventData(null, mouseOver));
    }

    protected void click(MouseButton button, boolean buttonDown) {
        this.fireListeners(new EventData(button, buttonDown));
    }

    private void fireListeners(EventData data) {
        int pointer = 0;
        while (pointer < this.listeners.size()) {
            this.listeners.get(pointer).eventOccurred(data);
            pointer++;
        }
    }

    private void chechMouseOver() {
        boolean mouseOverStatus = super.isMouseOver();
        if (this.mouseOver != mouseOverStatus) this.mouseOver(mouseOverStatus);
    }

    private void checkClicks() {
        Mouse mouse = UIMaster.getMouse();
        this.checkMouseButton(MouseButton.LEFT, mouse);
        this.checkMouseButton(MouseButton.MIDDLE, mouse);
        this.checkMouseButton(MouseButton.RIGHT, mouse);
    }

    private void checkMouseButton(MouseButton button, Mouse mouse) {
        if (mouse.isClickEvent(button)) this.click(button, true);
        else if (mouse.isReleaseEvent(button)) this.click(button, false);
    }

}
