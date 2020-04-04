package de.simagdo.game.gui.main;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.IGameLogic;
import de.simagdo.engine.graph.camera.Camera;
import de.simagdo.engine.inputsOutputs.userInput.Keyboard;
import de.simagdo.engine.inputsOutputs.userInput.Mouse;
import de.simagdo.engine.window.Window;
import de.simagdo.engine.window.WindowOptions;
import de.simagdo.game.DummyGame;
import de.simagdo.game.managing.GameConfigs;

public class GameManager {

    private static GameEngine engine;
    private static Camera camera;
    private static Keyboard keyboard;
    private static Mouse mouse;
    private static Window window;
    private static GameConfigs gameConfigs = new GameConfigs();
    private static boolean initialized = false;

    public void init() {
        if (!initialized) {
            try {
                IGameLogic gameLogic = new DummyGame();
                WindowOptions windowOptions = new WindowOptions();
                windowOptions.cullFace = false;
                windowOptions.showFPS = true;
                windowOptions.compatibleProfile = true;
                windowOptions.antialiasing = true;
                windowOptions.frustumCulling = false;
                engine = new GameEngine(gameConfigs.windowTitle, gameConfigs.windowWidth, gameConfigs.windowHeight, gameConfigs.vSync, gameConfigs.windowOptions, gameLogic);
                initialized = true;
                camera = new Camera();
                keyboard = engine.getKeyboard();
                mouse = engine.getMouse();
                window = engine.getWindow();
                System.out.println("Initialized: " + initialized);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static GameEngine getEngine() {
        return engine;
    }

    public static void setEngine(GameEngine gameEngine) {
        engine = gameEngine;
    }

    public static Camera getCamera() {
        return camera;
    }

    public static void setCamera(Camera cameraTemp) {
        camera = cameraTemp;
    }

    public static Keyboard getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(Keyboard keyboardTemp) {
        keyboard = keyboardTemp;
    }

    public static Mouse getMouse() {
        return mouse;
    }

    public static void setMouse(Mouse mouseTemp) {
        mouse = mouseTemp;
    }

    public static Window getWindow() {
        return window;
    }

    public static void setWindow(Window windowTemp) {
        window = windowTemp;
    }

    public static GameConfigs getGameConfigs() {
        return gameConfigs;
    }

    public static void setGameConfigs(GameConfigs configs) {
        gameConfigs = configs;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setInitialized(boolean initializedTemp) {
        initialized = initializedTemp;
    }
}