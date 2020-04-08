package de.simagdo.game.gui.main;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.graph.Renderer;
import de.simagdo.engine.graph.camera.Camera;
import de.simagdo.engine.graph.camera.CameraControls;
import de.simagdo.engine.world.World;
import de.simagdo.game.managing.GameConfigs;

public class GameManager {

    private static GameEngine engine;
    private static Camera camera;
    private static Renderer renderer;
    private static World world;

    public static void init(GameConfigs configs) throws Exception {
        engine = new GameEngine(configs);
        //renderer = new Renderer();
        //renderer.init(engine.getWindow());
        camera = setUpCamera();
    }

    private static Camera setUpCamera() {
        CameraControls controls = new CameraControls(engine.getMouse(), engine.getKeyboard());
        return new Camera(controls, engine.getWindow());
    }

    public static void update() {
        engine.update();
    }

    public static void cleanUp(){
        engine.cleanUp();
    }

    public static GameEngine getEngine() {
        return engine;
    }

    public static Camera getCamera() {
        return camera;
    }

    public static Renderer getRenderer() {
        return renderer;
    }

    public static World getWorld() {
        return world;
    }

}