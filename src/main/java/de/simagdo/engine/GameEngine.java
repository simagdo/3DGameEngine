package de.simagdo.engine;

import de.simagdo.engine.graph.camera.Camera;
import de.simagdo.engine.inputsOutputs.stateControl.StateManager;
import de.simagdo.engine.inputsOutputs.userInput.Keyboard;
import de.simagdo.engine.inputsOutputs.userInput.Mouse;
import de.simagdo.engine.window.Window;
import de.simagdo.game.managing.GameConfigs;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class GameEngine {

    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;
    private final Window window;
    private final Timer timer;
    private double lastFPS;
    private int fps;
    private final Keyboard keyboard;
    private final Mouse mouse;
    private final StateManager stateManager;
    GameConfigs configs;

    public GameEngine(GameConfigs configs) {
        this.configs = configs;
        this.window = this.setUpWindow();
        this.keyboard = new Keyboard(this.window.getWindowId());
        this.mouse = new Mouse(this.window);
        this.timer = new Timer(this.window.getFps());
        this.stateManager = new StateManager(configs.defaulState, configs.initialState);
        this.initOpenGL(this.window.getPixelWidth(), this.window.getPixelHeight());
    }

    private Window setUpWindow() {
        Window window = Window.newWindow(this.configs.windowWidth, this.configs.windowHeight, this.configs.windowTitle, this.configs.windowOptions)
                .setVSync(this.configs.vSync)
                .create();
        window.addSizeChangeListener((width, height) -> GL11.glViewport(0, 0, width, height));
        return window;
    }

    private void initOpenGL(int pixelWidth, int pixelHeight) {
        GL.createCapabilities();
        GL11.glViewport(0, 0, pixelWidth, pixelHeight);
    }

    public void update() {
        this.keyboard.update();
        this.mouse.update();
        this.window.update();
        this.timer.update();
        this.stateManager.updateState();
    }

    public void cleanUp() {
        this.window.cleanUp();
    }

    public static int getTargetFps() {
        return TARGET_FPS;
    }

    public static int getTargetUps() {
        return TARGET_UPS;
    }

    public Window getWindow() {
        return window;
    }

    public Timer getTimer() {
        return timer;
    }

    public double getLastFPS() {
        return lastFPS;
    }

    public int getFps() {
        return fps;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public GameConfigs getConfigs() {
        return configs;
    }

    public float getDeltaSeconds() {
        return this.timer.getDelta();
    }

}
