package de.simagdo.game;

import de.simagdo.engine.IGameLogic;
import de.simagdo.engine.MouseInput;
import de.simagdo.engine.Scene;
import de.simagdo.engine.SceneLight;
import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.graph.Renderer;
import de.simagdo.engine.graph.camera.Camera;
import de.simagdo.engine.graph.lights.DirectionalLight;
import de.simagdo.engine.graph.weather.Fog;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.SkyBox;
import de.simagdo.engine.loaders.assimp.StaticMeshesLoader;
import de.simagdo.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

    private final Renderer renderer;
    private GameItem[] gameItems;
    private Camera camera;
    private final Vector3f cameraInc;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.40f;
    private Scene scene;
    private float lightAngle;
    private float angleInc;
    private boolean firstTime;
    private boolean sceneChanged;

    private enum Sounds {
        FIRE
    }

    public DummyGame() {
        this.renderer = new Renderer();
        this.camera = new Camera();
        this.cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        this.angleInc = 0;
        this.lightAngle = 90;
        this.firstTime = true;
    }

    @Override
    public void init(Window window) throws Exception {
        this.renderer.init(window);

        this.scene = new Scene();

        Mesh[] houseMesh = StaticMeshesLoader.load("models/house/house.obj", "/models/house");
        GameItem house = new GameItem(houseMesh);

        Mesh[] terrainMesh = StaticMeshesLoader.load("models/terrain/terrain.obj", "/models/terrain");
        GameItem terrain = new GameItem(terrainMesh);
        terrain.setScale(100.0f);

        //Set Game Items
        this.scene.setGameItems(new GameItem[]{house, terrain});

        // Shadows
        this.scene.setRenderShadows(true);

        // Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
        this.scene.setFog(new Fog(true, fogColour, 0.02f));

        // Setup  SkyBox

        float skyBoxScale = 100.0f;

        SkyBox skyBox = new SkyBox("models/skybox.obj", new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
        skyBox.setScale(skyBoxScale);
        this.scene.setSkyBox(skyBox);

        //Setup Lights
        this.setupLights();

        this.camera.getPosition().x = -17.0f;
        this.camera.getPosition().y = 17.0f;
        this.camera.getPosition().z = -30.0f;
        this.camera.getRotation().x = 20.0f;
        this.camera.getRotation().y = 140.0f;

    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        this.scene.setSceneLight(sceneLight);

        //Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        //Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        this.cameraInc.set(0, 0, 0);
        this.sceneChanged = false;
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
            this.sceneChanged = true;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
            this.sceneChanged = true;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
            this.sceneChanged = true;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
            this.sceneChanged = true;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
            this.sceneChanged = true;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
            this.sceneChanged = true;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            this.angleInc -= 0.05f;
            this.sceneChanged = true;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            this.angleInc += 0.05f;
            this.sceneChanged = true;
        } else {
            this.angleInc = 0;
        }

    }

    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {
        if (mouseInput.isRightButtonPressed()) {
            //Update Camera based on mouse
            Vector2f rotVec = mouseInput.getDisplVec();
            this.camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            this.sceneChanged = true;
        }

        // Update camera position
        this.camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        this.lightAngle += this.angleInc;
        if (this.lightAngle < 0) this.lightAngle = 0;
        else if (this.lightAngle > 180) this.lightAngle = 180;

        float zValue = (float) Math.cos(Math.toRadians(this.lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(this.lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;

        //Update View Matrix
        this.camera.updateViewMatrix();

    }

    @Override
    public void render(Window window) {
        if (this.firstTime) {
            this.sceneChanged = true;
            this.firstTime = false;
        }
        this.renderer.render(window, this.camera, this.scene, this.sceneChanged);
    }

    @Override
    public void cleanUp() {
        this.renderer.cleanUp();
        for (Mesh mesh : scene.getMeshMap().keySet()) mesh.cleanUp();
    }

}
