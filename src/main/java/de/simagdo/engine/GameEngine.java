package de.simagdo.engine;

import de.simagdo.engine.window.Window;
import de.simagdo.engine.window.WindowOptions;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 75;
    public static final int TARGET_UPS = 30;
    private final Window window;
    private final Thread gameLoopThread;
    private final Timer timer;
    private final IGameLogic gameLogic;
    private final MouseInput mouseInput;

    public GameEngine(String windowTitle, boolean vSync, WindowOptions windowOptions, IGameLogic gameLogic) throws Exception {
        this(windowTitle, 0, 0, vSync, windowOptions, gameLogic);
    }

    public GameEngine(String windowTitle, int width, int height, boolean vSync, WindowOptions windowOptions, IGameLogic gameLogic) throws Exception {
        this.gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        this.window = new Window(windowTitle, width, height, vSync, windowOptions);
        this.gameLogic = gameLogic;
        timer = new Timer();
        this.mouseInput = new MouseInput();
    }

    public void start() {
        String osName = System.getProperty("os.name");
        if (osName.contains("Mac")) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
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

    protected void init() throws Exception {
        this.window.init();
        this.timer.init();
        this.mouseInput.init(this.window);
        this.gameLogic.init(this.window);
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
        this.gameLogic.update(interval, this.mouseInput);
    }

    protected void render() {
        this.gameLogic.render(this.window);
        this.window.update();
    }

    protected void cleanUp() {
        this.gameLogic.cleanUp();
    }

}
