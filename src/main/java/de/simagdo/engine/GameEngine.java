package de.simagdo.engine;

import de.simagdo.engine.graph.camera.Camera;
import de.simagdo.engine.inputsOutputs.stateControl.EmptyState;
import de.simagdo.engine.inputsOutputs.stateControl.StateManager;
import de.simagdo.engine.inputsOutputs.userInput.Keyboard;
import de.simagdo.engine.inputsOutputs.userInput.Mouse;
import de.simagdo.engine.window.Window;
import de.simagdo.engine.window.WindowOptions;
import de.simagdo.game.gui.main.GameEngineUI;
import de.simagdo.game.managing.GameConfigs;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;
    private final Window window;
    private final Timer timer;
    private final IGameLogic gameLogic;
    private final MouseInput mouseInput;
    private double lastFPS;
    private int fps;
    private String windowTitle;
    private final Thread gameLoopThread;
    private GameEngineUI gameEngineUI;
    private final Keyboard keyboard;
    private final Mouse mouse;
    private final StateManager stateManager = new StateManager(new EmptyState(), new EmptyState());
    private Camera camera;

    public GameEngine(GameConfigs configs) throws Exception {
        this(configs.windowTitle, configs.windowWidth, configs.windowHeight, configs.vSync, configs.windowOptions, configs.gameLogic);
    }

    public GameEngine(String windowTitle, boolean vSync, WindowOptions windowOptions, IGameLogic gameLogic) throws Exception {
        this(windowTitle, 0, 0, vSync, windowOptions, gameLogic);
    }

    public GameEngine(String windowTitle, int width, int height, boolean vSync, WindowOptions windowOptions, IGameLogic gameLogic) throws Exception {
        this.windowTitle = windowTitle;
        this.gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        this.window = new Window(windowTitle, width, height, vSync, windowOptions);
        this.gameLogic = gameLogic;
        timer = new Timer();
        this.mouseInput = new MouseInput();
        this.gameEngineUI = new GameEngineUI(this);
        this.keyboard = new Keyboard();
        this.mouse = new Mouse(this.window);
        this.camera = new Camera();
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if (osName.contains("Mac")) {
            this.gameLoopThread.run();
        } else {
            this.gameLoopThread.start();
        }
    }

    @Override
    public void run() {
        try {
            this.init();
            this.gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            this.cleanUp();
        }
    }

    public void close() {
        this.gameLoopThread.stop();
    }

    protected void init() throws Exception {
        this.window.init();
        this.timer.init();
        this.mouseInput.init(this.window);
        this.gameLogic.init(this.window);
        System.out.println(this.window.getWindowHandle());
        this.keyboard.init(this.window);
        this.mouse.init();
        this.lastFPS = this.timer.getTime();
        this.fps = 0;
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        while (!window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            this.input();

            while (accumulator >= interval) {
                this.update(interval);
                accumulator -= interval;
            }

            this.render();

            if (!window.isvSync()) {
                this.sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = this.timer.getLastLoopTime() + loopSlot;
        while (this.timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void input() {
        this.mouseInput.input(this.window);
        this.gameLogic.input(this.window, this.mouseInput);
    }

    protected void update(float interval) {
        this.gameLogic.update(interval, this.mouseInput, this.window);
    }

    protected void render() {
        if (this.window.getWindowOptions().showFPS && this.timer.getLastLoopTime() - this.lastFPS > 1) {
            this.lastFPS = this.timer.getLastLoopTime();
            this.window.setWindowTitle(this.windowTitle + " - " + this.fps + " FPS");
            this.fps = 0;
        }
        fps++;
        this.gameLogic.render(this.window);
        this.window.update();
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public StateManager getStateManager() {
        return this.stateManager;
    }

    public Window getWindow() {
        return window;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    protected void cleanUp() {
        this.gameLogic.cleanUp();
    }

}
