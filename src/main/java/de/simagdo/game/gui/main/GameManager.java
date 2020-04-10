package de.simagdo.game.gui.main;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.Scene;
import de.simagdo.engine.SceneLight;
import de.simagdo.engine.graph.Attenuation;
import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.graph.Renderer;
import de.simagdo.engine.graph.camera.Camera;
import de.simagdo.engine.graph.camera.CameraControls;
import de.simagdo.engine.graph.lights.DirectionalLight;
import de.simagdo.engine.graph.lights.PointLight;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.Terrain;
import de.simagdo.engine.loaders.assimp.StaticMeshesLoader;
import de.simagdo.engine.world.World;
import de.simagdo.game.managing.GameConfigs;
import org.joml.Vector3f;

public class GameManager {

    private static GameEngine engine;
    private static Camera camera;
    private static Renderer renderer;
    private static World world;
    private static Scene scene;

    public static void init(GameConfigs configs) throws Exception {
        engine = new GameEngine(configs);
        camera = setUpCamera();
        scene = setUpScene();
        setupLights();
        renderer = setUpRenderer();
        world = setUpWorld();
    }

    private static Renderer setUpRenderer() throws Exception {
        Renderer renderer = new Renderer();
        renderer.init(getEngine().getWindow());

        renderer.render(getEngine().getWindow(), getCamera(), scene, false);

        return renderer;
    }

    private static Scene setUpScene() throws Exception {
        Scene scene = new Scene();

        /*Mesh[] terrainMesh = StaticMeshesLoader.load("models/terrain/terrain.obj", "/models/terrain");
        GameItem terrain = new GameItem(terrainMesh);
        terrain.setScale(100.0f);
        terrain.setPosition(0.25f,6f,0.25f);

        scene.setGameItems(new GameItem[]{terrain});*/

        Terrain terrain = new Terrain(3, 10, -0.1f, 0.1f, "/textures/heightmap.png", "/textures/terrain.png", 40);
        scene.setGameItems(terrain.getGameItems());

        return scene;
    }

    private static void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        //Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        //Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);

        Vector3f pointLightPos = new Vector3f(0.0f, 25.0f, 0.0f);
        Vector3f pointLightColor = new Vector3f(0.f, 1.0f, 0.0f);
        Attenuation attenuation = new Attenuation(1, 0.0f, 0);
        PointLight pointLight = new PointLight(pointLightColor, pointLightPos, lightIntensity, attenuation);
        sceneLight.setPointLights(new PointLight[]{pointLight});

    }

    private static Camera setUpCamera() {
        CameraControls controls = new CameraControls(engine.getMouse(), engine.getKeyboard());
        Camera camera = new Camera(controls, getEngine().getWindow());
        camera.getPosition().x = 0.0f;
        camera.getPosition().y = 0.0f;
        camera.getPosition().z = -0.2f;
        return camera;
    }

    private static World setUpWorld() {
        return new World().loadWorld();
    }

    public static void update() {
        getEngine().update();
        getCamera().move(getEngine().getDeltaSeconds());
        //getRenderer().render(getEngine().getWindow(), getCamera(), getScene(), false);
    }

    public static void cleanUp() {
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

    public static Scene getScene() {
        return scene;
    }
}