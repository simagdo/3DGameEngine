package de.simagdo.game;

import de.simagdo.engine.*;
import de.simagdo.engine.graph.*;
import de.simagdo.engine.graph.lights.DirectionalLight;
import de.simagdo.engine.graph.text.Texture;
import de.simagdo.engine.graph.weather.Fog;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.SkyBox;
import de.simagdo.engine.items.Terrain;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

    private final Renderer renderer;
    private GameItem[] gameItems;
    private Camera camera;
    private final Vector3f cameraInc;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.05f;
    private Hud hud;
    private Scene scene;
    private float lightAngle;
    private Terrain terrain;

    public DummyGame() {
        renderer = new Renderer();
        this.camera = new Camera();
        this.cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        this.lightAngle = -35;
    }

    @Override
    public void init(Window window) throws Exception {
        this.renderer.init(window);

        this.scene = new Scene();

        float reflectance = 0.65f;
        Texture normalMap = new Texture("/textures/rock_normals.png");

        Mesh quadMesh1 = OBJLoader.loadMesh("/models/quad.obj");
        Texture texture = new Texture("/textures/rock.png");
        Material quadMaterial1 = new Material(texture, reflectance);
        quadMesh1.setMaterial(quadMaterial1);
        GameItem quadGameItem1 = new GameItem(quadMesh1);
        quadGameItem1.setPosition(-3f, 0, 0);
        quadGameItem1.setScale(2.0f);
        quadGameItem1.setRotation(90, 0, 0);

        Mesh quadMesh2 = OBJLoader.loadMesh("/models/quad.obj");
        Material quadMaterial2 = new Material(texture, reflectance);
        quadMaterial2.setNormalMap(normalMap);
        quadMesh2.setMaterial(quadMaterial2);
        GameItem quadGameItem2 = new GameItem(quadMesh2);
        quadGameItem2.setPosition(3f, 0, 0);
        quadGameItem2.setScale(2.0f);
        quadGameItem2.setRotation(90, 0, 0);

        this.scene.setGameItems(new GameItem[]{quadGameItem1, quadGameItem2});

        this.setupLights();

        this.camera.getPosition().y = 5.0f;
        this.camera.getRotation().x = 90f;
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(1, 1, 0);
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity));
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
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
        if ( window.isKeyPressed(GLFW_KEY_LEFT)) {
            lightAngle -= 2.5f;
            if ( lightAngle < -90 ) {
                lightAngle = -90;
            }
        } else if ( window.isKeyPressed(GLFW_KEY_RIGHT)) {
            lightAngle += 2.5f;
            if ( lightAngle > 90 ) {
                lightAngle = 90;
            }
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        // Update camera position
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        // Check if there has been a collision. If true, set the y position to
        // the maximum height
        float height = terrain != null ? terrain.getHeight(camera.getPosition()) : -Float.MAX_VALUE;
        if (camera.getPosition().y <= height) {
            camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
        }

        // Update directional light direction, intensity and colour
        SceneLight sceneLight = scene.getSceneLight();
        DirectionalLight directionalLight = sceneLight.getDirectionalLight();
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        if (this.hud != null)
            this.hud.updateSize(window);
        this.renderer.render(window, this.camera, this.scene, this.hud);
    }

    @Override
    public void cleanup() {
        this.renderer.cleanUp();
        for (Mesh mesh : scene.getMeshMap().keySet()) mesh.cleanUp();
        if (this.hud != null)
            this.hud.cleanup();
    }

}