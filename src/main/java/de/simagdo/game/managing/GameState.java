package de.simagdo.game.managing;

import de.simagdo.engine.inputsOutputs.stateControl.State;

public enum GameState implements State {

    SPLASH_SCREEN(),
    MAIN_MENU(),
    UI(),
    NORMAL(),
    CAMERA();

    @Override
    public int getPriority() {
        return 0;
    }
}
