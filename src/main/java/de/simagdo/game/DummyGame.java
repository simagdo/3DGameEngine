package de.simagdo.game;

import de.simagdo.engine.*;
import de.simagdo.engine.graph.*;
import de.simagdo.engine.graph.animation.AnimatedGameItem;
import de.simagdo.engine.graph.lights.DirectionalLight;
import de.simagdo.engine.graph.particles.FlowParticleEmitter;
import de.simagdo.engine.graph.particles.Particle;
import de.simagdo.engine.graph.text.Texture;
import de.simagdo.engine.graph.weather.Fog;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.SkyBox;
import de.simagdo.engine.items.Terrain;
import de.simagdo.engine.loaders.md5.MD5AnimModel;
import de.simagdo.engine.loaders.md5.MD5Loader;
import de.simagdo.engine.loaders.md5.MD5Model;
import de.simagdo.engine.loaders.obj.OBJLoader;
import de.simagdo.engine.window.Window;
import org.joml.Matrix4f;
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
    private static final float CAMERA_POS_STEP = 0.10f;
    private Hud hud;
    private Scene scene;
    private float lightAngle;
    private float angleInc;
    private Terrain terrain;

    public DummyGame() {
        this.renderer = new Renderer();
        this.camera = new Camera();
        this.cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        this.angleInc = 0;
        this.lightAngle = 45;
    }

    @Override
    public void init(Window window) throws Exception {
        this.renderer.init(window);

        this.scene = new Scene();

        //Setup GameItems
        float blockScale = 0.5f;
        float skyBoxScale = 50.0f;
        float extension = 2.0f;

        float startX = extension * (-skyBoxScale + blockScale);
        float startY = -1.0f;
        float startZ = extension * (skyBoxScale - blockScale);
        float inc = blockScale * 2;

        float posX = startX;
        float posZ = startZ;
        float incY;

        int NUM_ROWS = (int) (extension * skyBoxScale * 2 / inc);
        int NUM_COLS = (int) (extension * skyBoxScale * 2 / inc);

        GameItem[] gameItems = new GameItem[NUM_ROWS * NUM_COLS];

        float reflectance = 1f;
        int instances = NUM_ROWS * NUM_COLS;
        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj", instances);
        Texture texture = new Texture("/textures/grassblock.png");
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                GameItem gameItem = new GameItem(mesh);
                gameItem.setScale(blockScale);
                incY = Math.random() > 0.9f ? blockScale * 2 : 0f;
                gameItem.setPosition(posX, startY + incY, posZ);
                gameItems[i * NUM_COLS + j] = gameItem;
                posX += inc;
            }
            posX = startX;
            posZ -= inc;
        }

        this.scene.setGameItems(gameItems);

        this.scene.setRenderShadows(false);

        //Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
        this.scene.setFog(new Fog(true, fogColour, 0.05f));

        //Setup SkyBox
        SkyBox skyBox = new SkyBox("/models/skybox.obj", new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
        skyBox.setScale(skyBoxScale);
        this.scene.setSkyBox(skyBox);

        //Setup Lights
        this.setupLights();

        this.camera.getPosition().x = 0.25f;
        this.camera.getPosition().y = 6.5f;
        this.camera.getPosition().z = 6.5f;
        this.camera.getRotation().x = 25;
        this.camera.getRotation().y = -1;

        this.hud = new Hud("");

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
        directionalLight.setShadowPosMult(5);
        directionalLight.setOrthoCoords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
        sceneLight.setDirectionalLight(directionalLight);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        this.cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            this.angleInc -= 0.05f;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            this.angleInc += 0.05f;
        } else {
            this.angleInc = 0;
        }

    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            this.camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        // Update camera position
        Vector3f prevPos = new Vector3f(camera.getPosition());
        this.camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        // Check if there has been a collision. If true, set the y position to
        // the maximum height
        float height = this.terrain != null ? this.terrain.getHeight(this.camera.getPosition()) : -Float.MAX_VALUE;
        if (this.camera.getPosition().y <= height) {
            this.camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
        }

        this.lightAngle += this.angleInc;
        if (this.lightAngle < 0) this.lightAngle = 0;
        else if (this.lightAngle > 180) this.lightAngle = 180;

        float zValue = (float) Math.cos(Math.toRadians(this.lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(this.lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        this.hud.setStatusText("X: " + this.camera.getPosition().x + ", Y: " + this.camera.getPosition().y + ", Z: " + this.camera.getPosition().z + ", RotX: " + this.camera.getRotation().x + ", RotY: " + this.camera.getRotation().y + ", RotZ: " + this.camera.getRotation().z);
    }

    @Override
    public void render(Window window) {
        if (this.hud != null)
            this.hud.updateSize(window);
        renderer.render(window, this.camera, this.scene, this.hud);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        for (Mesh mesh : scene.getMeshMap().keySet()) mesh.cleanUp();
        if (this.hud != null)
            this.hud.cleanUp();
    }

}
