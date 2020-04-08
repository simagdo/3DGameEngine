package de.simagdo.engine.graph.camera;

import de.simagdo.engine.inputsOutputs.userInput.Keyboard;
import de.simagdo.engine.inputsOutputs.userInput.Mouse;
import de.simagdo.engine.inputsOutputs.userInput.MouseButton;
import de.simagdo.engine.inputsOutputs.userInput.UserInput;
import de.simagdo.game.gui.main.GameManager;
import de.simagdo.game.managing.GameState;
import org.lwjgl.glfw.GLFW;

public class CameraControls implements ICameraControls, UserInput {

    private final Mouse mouse;
    private final Keyboard keyboard;
    private boolean enabled = false;
    private boolean keyboardEnabled = false;

    public CameraControls(Mouse mouse, Keyboard keyboard) {
        GameManager.getEngine().getStateManager().registerUser(this, GameState.NORMAL);
        this.mouse = mouse;
        this.keyboard = keyboard;
    }

    public void enable(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public float getZoomInput() {
        return !this.enabled ? 0 : this.mouse.getScroll();
    }

    @Override
    public float getPitchInput() {
        if (!this.mouse.isButtonDown(MouseButton.MIDDLE) || !this.enabled) return 0;
        GameManager.getEngine().getStateManager().suggestState(GameState.CAMERA, false);
        return this.mouse.getDy();
    }

    @Override
    public float getYawInput() {
        if (!this.mouse.isButtonDown(MouseButton.MIDDLE) || !this.enabled) return 0;
        GameManager.getEngine().getStateManager().suggestState(GameState.CAMERA, false);
        return this.mouse.getDx();
    }

    @Override
    public boolean goRight() {
        return !this.keyboardEnabled ? false : this.keyboard.isKeyDown(GLFW.GLFW_KEY_D);
    }

    @Override
    public boolean goLeft() {
        return !this.keyboardEnabled ? false : this.keyboard.isKeyDown(GLFW.GLFW_KEY_A);
    }

    @Override
    public boolean goForwards() {
        return !this.keyboardEnabled ? false : this.keyboard.isKeyDown(GLFW.GLFW_KEY_W);
    }

    @Override
    public boolean goBackwards() {
        return !this.keyboardEnabled ? false : this.keyboard.isKeyDown(GLFW.GLFW_KEY_S);
    }

    @Override
    public void enableKeyboardUse(boolean enable) {
        this.keyboardEnabled = enabled;
    }

    @Override
    public void enableMouseUse(boolean enable) {
        this.enabled = enable;
    }
}
