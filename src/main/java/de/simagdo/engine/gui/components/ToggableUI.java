package de.simagdo.engine.gui.components;

import de.simagdo.engine.inputsOutputs.userInput.MouseButton;

import java.util.ArrayList;
import java.util.List;

public abstract class ToggableUI extends ClickableUI {

    private final List<ToggleListener> listeners = new ArrayList<>();
    private boolean canManuallyToggleOff = true;
    private boolean on;

    protected ToggableUI(boolean on) {
        this.on = on;
    }

    public void disableManualToggleOff() {
        this.canManuallyToggleOff = false;
    }

    public void toggle() {
        this.on = !this.on;
        this.fireListeners();
    }

    public boolean isOn() {
        return this.on;
    }

    public void addToggleListener(ToggleListener listener) {
        this.listeners.add(listener);
    }

    @Override
    protected void click(MouseButton button, boolean buttonDown) {
        super.click(button, buttonDown);
        if (buttonDown && button == MouseButton.LEFT && (this.canManuallyToggleOff || !this.on)) this.toggle();
    }

    private void fireListeners() {
        int pointer = 0;
        while (pointer < this.listeners.size()) {
            this.listeners.get(pointer).toggleEvent(this.on);
            pointer++;
        }
    }

}
