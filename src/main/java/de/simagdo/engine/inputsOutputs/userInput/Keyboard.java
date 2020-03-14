package de.simagdo.engine.inputsOutputs.userInput;

import de.simagdo.engine.window.Window;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class Keyboard {

    private Set<Integer> keysPressedThisFrame = new HashSet<>();
    private Set<Integer> keysRepeatedThisFrame = new HashSet<>();
    private Set<Integer> keysReleasedThisFrame = new HashSet<>();
    private Set<Integer> keysDown = new HashSet<>();
    private String charsThisFrame = "";

    public void init(Window window) {
        this.addKeyListener(window.getWindowHandle());
        this.addTextListener(window.getWindowHandle());
    }

    public void update() {
        this.keysPressedThisFrame.clear();
        this.keysRepeatedThisFrame.clear();
        this.keysReleasedThisFrame.clear();
        this.charsThisFrame = "";
    }

    public boolean isKeyDown(int key) {
        return this.keysDown.contains(key);
    }

    public String getChars() {
        return this.charsThisFrame;
    }

    public boolean keyPressEvent(int key) {
        return this.keysPressedThisFrame.contains(key);
    }

    public boolean keyPressEvent(int key, boolean checkRepeats) {
        return this.keysPressedThisFrame.contains(key) || (checkRepeats && this.keysRepeatedThisFrame.contains(key));
    }

    private void reportKeyPress(int key) {
        this.keysDown.add(key);
        this.keysPressedThisFrame.add(key);
    }

    private void reportKeyReleased(int key) {
        this.keysDown.remove((Integer) key);
        this.keysReleasedThisFrame.add(key);
    }

    private void addTextListener(long windowID) {
        GLFW.glfwSetCharCallback(windowID, (window, unicode) -> {
            this.charsThisFrame += (char) unicode;
        });
    }

    private void addKeyListener(long windowID) {
        GLFW.glfwSetKeyCallback(windowID, (window, key, scanCode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                this.reportKeyPress(key);
            } else if (action == GLFW.GLFW_RELEASE) {
                this.reportKeyReleased(key);
            } else if (action == GLFW.GLFW_REPEAT) {
                this.keysRepeatedThisFrame.add(key);
            }
        });
    }

}
