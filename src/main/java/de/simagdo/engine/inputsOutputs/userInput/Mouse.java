package de.simagdo.engine.inputsOutputs.userInput;

import de.simagdo.engine.window.Window;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {

    private final Window window;

    private Set<Integer> buttonsDown = new HashSet<>();
    private Set<Integer> buttonsClickedThisFrame = new HashSet<>();
    private Set<Integer> buttonsReleasedThisFrame = new HashSet<>();

    private float x, y;
    private float dx, dy;
    private float scroll;
    private float lastX, lastY;

    public Mouse(Window window) {
        this.window = window;
    }

    public void init() {
        this.addMoveListener(window.getWindowId());
        this.addClickListener(window.getWindowId());
        this.addScrollListener(window.getWindowId());
    }

    public boolean isButtonDown(MouseButton button) {
        return this.buttonsDown.contains(button.getGlfwId());
    }

    public boolean isClickEvent(MouseButton button) {
        return this.buttonsClickedThisFrame.contains(button.getGlfwId());
    }

    public boolean isReleaseEvent(MouseButton button) {
        return this.buttonsReleasedThisFrame.contains(button.getGlfwId());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public float getScroll() {
        return scroll;
    }

    public void update() {
        this.buttonsClickedThisFrame.clear();
        this.buttonsReleasedThisFrame.clear();
        this.updateDeltas();
        this.scroll = 0;
    }

    private void reportButtonClick(int button) {
        this.buttonsClickedThisFrame.add(button);
        this.buttonsDown.add(button);
    }

    private void reportButtonRelease(int button) {
        this.buttonsReleasedThisFrame.add(button);
        this.buttonsDown.remove((Integer) button);
    }

    private void updateDeltas() {
        this.dx = this.x - this.lastX;
        this.dy = this.y - this.lastY;
        this.lastX = this.x;
        this.lastY = this.y;
    }

    private void addMoveListener(long windowId) {
        GLFW.glfwSetCursorPosCallback(windowId, (currentWidnow, xPos, yPos) -> {
            this.x = (float) (xPos / this.window.getDesiredWidth());
            this.y = (float) (yPos / this.window.getDesiredHeight());
        });
    }

    private void addClickListener(long windowId) {
        GLFW.glfwSetMouseButtonCallback(windowId, (window, button, action, mods) -> {
            if (action == GLFW_PRESS) {
                this.reportButtonClick(button);
            } else if (action == GLFW_RELEASE) {
                this.reportButtonRelease(button);
            }
        });
    }

    private void addScrollListener(long windowId) {
        glfwSetScrollCallback(windowId, (window, scrollX, scrollY) -> {
            this.scroll = (float) scrollY;
        });
    }

}
