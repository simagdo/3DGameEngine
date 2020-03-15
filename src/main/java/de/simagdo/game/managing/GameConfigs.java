package de.simagdo.game.managing;

import de.simagdo.engine.IGameLogic;
import de.simagdo.engine.inputsOutputs.stateControl.EmptyState;
import de.simagdo.engine.inputsOutputs.stateControl.State;
import de.simagdo.engine.window.WindowOptions;

public class GameConfigs {

    public int windowWidth = 1280;
    public int windowMinWidth = 600;
    public int windowHeight = 720;
    public int windowMinHeight = 350;
    public WindowOptions windowOptions = new WindowOptions();
    public float uiSize = 1;
    public boolean fullscreen = false;
    public String windowTitle = "The lonely Wizard";
    public int fps = 100;
    public boolean vSync = true;
    public State initialState = new EmptyState();
    public State defaulState = new EmptyState();
    public IGameLogic gameLogic;

    public static GameConfigs getDefaultConfig() {
        return new GameConfigs();
    }

}
