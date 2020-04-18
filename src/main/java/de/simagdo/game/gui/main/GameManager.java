package de.simagdo.game.gui.main;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.Scene;
import de.simagdo.engine.SceneLight;
import de.simagdo.engine.graph.Attenuation;
import de.simagdo.engine.graph.Renderer;
import de.simagdo.engine.graph.camera.Camera;
import de.simagdo.engine.graph.camera.CameraControls;
import de.simagdo.engine.graph.lights.DirectionalLight;
import de.simagdo.engine.graph.lights.PointLight;
import de.simagdo.engine.inputsOutputs.userInput.Keyboard;
import de.simagdo.engine.toolbox.misc.SmoothFloat;
import de.simagdo.engine.world.World;
import de.simagdo.game.managing.GameConfigs;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class GameManager {

    private static GameEngine engine;
    private static Camera camera;
    private static Renderer renderer;
    private static World world;
    private static Scene scene;
    private static Thread gameLoopThread;

    public static void init(GameConfigs configs) throws Exception {
        gameLoopThread = new Thread("GAME_LOOP_THREAD");
        engine = new GameEngine(configs);
        camera = setUpCamera();
        scene = setUpScene();
        setupLights();
        world = setUpWorld();
        renderer = setUpRenderer();
    }

    private static Renderer setUpRenderer() throws Exception {
        Renderer renderer = new Renderer();
        renderer.init(getEngine().getWindow());

        //renderer.render(getEngine().getWindow(), getCamera(), scene, true);

        return renderer;
    }

    private static Scene setUpScene() throws Exception {
        Scene scene = new Scene();

        /*Mesh[] terrainMesh = StaticMeshesLoader.load("models/terrain/terrain.obj", "/models/terrain");
        GameItem terrainTest = new GameItem(terrainMesh);
        terrainTest.setScale(100.0f);
        terrainTest.setPosition(0.25f,6f,0.25f);

        //Save GameItem in File for Test purpose
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\simag\\IdeaProjects\\3DGameEngine\\TerrainTest.txt"));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            //Write Object to File
            objectOutputStream.writeObject(terrainTest);

            objectOutputStream.close();
            fileOutputStream.close();

        } catch (IOException e){
            e.printStackTrace();
        }

        scene.setGameItems(new GameItem[]{terrainTest});*/

        /*Terrain terrain = new Terrain(3, 10, -0.1f, 0.1f, "/textures/heightmap.png", "/textures/terrain.png", 40);
        scene.setGameItems(terrain.getGameItems());

        SkyBox skyBox = new SkyBox("models/skybox.obj", "/textures/skybox.png");
        skyBox.setScale(50.0f);
        scene.setSkyBox(skyBox);*/

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
        camera.setPitch(new SmoothFloat(0, 0));
        return camera;
    }

    private static World setUpWorld() throws Exception {
        World world = new World(getScene());
        world.generateWorld();
        world.loadWorld();
        return world;
    }

    public static void run() {
        gameLoopThread.run();
        try {
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanUp();
        }
    }

    protected static void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / GameEngine.TARGET_UPS;
        boolean running = true;

        while (running && !getEngine().getWindow().closeButtonPressed()) {
            elapsedTime = getEngine().getTimer().getDelta();
            accumulator += elapsedTime;

            update();

        }

    }

    protected static void render() {
        getEngine().getWindow().update();
    }

    public static void update() {
        getCamera().move(getEngine().getDeltaSeconds());
        getRenderer().render(getEngine().getWindow(), getCamera(), getScene(), false);
        getEngine().update();
    }

    public static void cleanUp() {
        getRenderer().cleanUp();
        getEngine().cleanUp();
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

    public static boolean readyToClose() {
        return getEngine().getWindow().closeButtonPressed();
    }

}