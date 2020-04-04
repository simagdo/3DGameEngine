package de.simagdo.engine.entities;

import de.simagdo.engine.inputsOutputs.userInput.Keyboard;
import de.simagdo.engine.items.GameItem;
import de.simagdo.game.gui.main.GameManager;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Entity {

    private static final float RUN_SPEED = 50;
    private static final float TURN_SPEED = 160;
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;
    private boolean running = false;
    private boolean isInAir = false;
    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;
    private GameItem gameItem;
    private Vector3f position;
    private Vector3f rotation;
    private float scale;
    private GameManager gameManager;
    private Keyboard keyboard;

    public Player(GameItem gameItem, Vector3f position, Vector3f rotation, float scale) {
        super(gameItem, position, rotation, scale);
        this.gameManager = new GameManager();
        this.gameItem = gameItem;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.keyboard = GameManager.getKeyboard();
        GameManager.getCamera().setPlayer(this);
    }

    @Override
    public void move() {
        //Check Keyboard Input
        this.checkKeyboardInput();

        float frameTime = 0.09f;

        //Increase Rotation
        super.increaseRotation(0, this.currentTurnSpeed * frameTime, 0);

        //Increase Position
        float distance = this.currentSpeed * frameTime;
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotation().y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotation().y)));
        //System.out.println("DX: " + dx + ", DZ: " + dz + ", TurnSpeed: " + this.currentTurnSpeed + ", CurrentSpeed: " + this.currentSpeed);
        super.increasePosition(dx, 0, dz);

        this.upwardsSpeed += GRAVITY * frameTime;
        super.increasePosition(0, upwardsSpeed * frameTime, 0);
        if (super.getPosition().y < 20) {
            this.upwardsSpeed = 0;
            this.isInAir = false;
            super.getPosition().y = 20;
        }

        //Update Camera
        GameManager.getCamera().move();

    }

    @Override
    public void jump() {
        if (!this.isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            this.isInAir = true;
        }
    }

    @Override
    public void checkKeyboardInput() {
        if (GameManager.isInitialized()) {
            if (keyboard.isKeyDown(GLFW_KEY_W)) this.currentSpeed = RUN_SPEED;
            else if (keyboard.isKeyDown(GLFW_KEY_S)) this.currentSpeed = -RUN_SPEED;
            else this.currentSpeed = 0;

            if (keyboard.isKeyDown(GLFW_KEY_D)) this.currentTurnSpeed = -TURN_SPEED;
            else if (keyboard.isKeyDown(GLFW_KEY_A)) this.currentTurnSpeed = TURN_SPEED;
            else this.currentTurnSpeed = 0;

            if (keyboard.isKeyDown(GLFW_KEY_SPACE)) this.jump();
        }
    }

    public GameItem getGameItem() {
        return this.gameItem;
    }
}
