package de.simagdo.engine.gui.mainUI;

import de.simagdo.engine.inputsOutputs.userInput.Mouse;

public class UIMaster {

    private static final UIContainer CONTAINER = new UIContainer();
    private static boolean mouseInteractionEnabled=true;
    private static Mouse currentMouse;

    public static Mouse getMouse() {
        return currentMouse;
    }

    public static UIContainer getContainer() {
        return CONTAINER;
    }

    public static void setMouseInteractionEnabled(boolean mouseInteractionEnabled) {
        UIMaster.mouseInteractionEnabled = mouseInteractionEnabled;
    }

    public static boolean isMouseInUI(){
        return CONTAINER.isMouseOver();
    }

}
