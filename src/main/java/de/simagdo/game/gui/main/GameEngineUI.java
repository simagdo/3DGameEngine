package de.simagdo.game.gui.main;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.gui.mainUI.UIInputControl;
import de.simagdo.engine.inputsOutputs.userInput.Keyboard;
import de.simagdo.game.gui.gameMenu.GameMenuUI;
import org.lwjgl.glfw.GLFW;

public class GameEngineUI {

    private final GameEngine engine;
    private final GameMenuUI mainMenu;
    private final UIInputControl uiInput;

    public GameEngineUI(GameEngine engine) {
        this.engine = engine;
        this.uiInput = new UIInputControl();
        this.mainMenu = new GameMenuUI(engine);
    }

    public void update() {
        Keyboard keyboard = this.engine.getKeyboard();
        if (keyboard.keyPressEvent(GLFW.GLFW_KEY_ESCAPE)) {
            System.out.println("Updating UI...");
            this.mainMenu.display(!this.mainMenu.isShown());
        }
        uiInput.checkActive();
    }

}
