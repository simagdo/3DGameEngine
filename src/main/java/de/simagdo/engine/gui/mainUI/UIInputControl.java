package de.simagdo.engine.gui.mainUI;

import de.simagdo.engine.inputsOutputs.userInput.UserInput;

public class UIInputControl implements UserInput {

    @Override
    public void enableKeyboardUse(boolean enable) {

    }

    @Override
    public void enableMouseUse(boolean enable) {
        UIMaster.setMouseInteractionEnabled(enable);
    }

    public void checkActive(){

    }

}
