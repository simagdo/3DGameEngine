package de.simagdo.engine.gui.components;

import de.simagdo.engine.inputsOutputs.userInput.MouseButton;

public class EventData {

    private final MouseButton button;
    private final boolean eventState;

    public EventData(MouseButton button, boolean eventState) {
        this.button = button;
        this.eventState = eventState;
    }

    public boolean isClick(MouseButton button) {
        return this.eventState && this.button == button;
    }

    public boolean isRelease(MouseButton button) {
        return !this.eventState && this.button == button;
    }

    public boolean isMouseOver() {
        return this.eventState && this.button == null;
    }

    public boolean isMouseOff() {
        return !this.eventState && this.button == null;
    }

}
