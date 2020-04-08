package de.simagdo.engine;

import de.simagdo.engine.window.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

//TODO Remove Class later on
public class MouseInput {

    private final Vector2d previousPos;
    private final Vector2d currentPos;
    private final Vector2f displVec;
    private boolean inWindow = false;
    private boolean leftButtonPressed = false;
    private boolean rightButtonPressed = false;

    public MouseInput() {
        this.previousPos = new Vector2d(-1, -1);
        this.currentPos = new Vector2d(0, 0);
        this.displVec = new Vector2f();
    }

    public void init(Window window) {
        /*glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xPos, yPos) -> {
            currentPos.x = xPos;
            currentPos.y = yPos;
        });
        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
            inWindow = entered;
        });
        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });*/
    }

    public Vector2d getPreviousPos() {
        return previousPos;
    }

    public Vector2d getCurrentPos() {
        return currentPos;
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public boolean isInWindow() {
        return inWindow;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public void input(Window window) {
        this.displVec.x = 0;
        this.displVec.y = 0;

        if (this.previousPos.x > 0 && this.previousPos.y > 0 && this.inWindow) {
            double deltaX = this.currentPos.x - this.previousPos.x;
            double deltaY = this.currentPos.y - this.previousPos.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if (rotateX) this.displVec.x = (float) deltaX;
            if (rotateY) this.displVec.y = (float) deltaY;
        }

        this.previousPos.x = this.currentPos.x;
        this.previousPos.y = this.currentPos.y;

    }

}
