package de.simagdo.game;

import de.simagdo.engine.*;
import de.simagdo.engine.graph.HeightMapMesh;
import de.simagdo.engine.graph.Material;
import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.graph.Renderer;
import de.simagdo.engine.graph.camera.Camera;
import de.simagdo.engine.graph.lights.DirectionalLight;
import de.simagdo.engine.graph.particles.FlowParticleEmitter;
import de.simagdo.engine.graph.particles.Particle;
import de.simagdo.engine.graph.text.Texture;
import de.simagdo.engine.graph.weather.Fog;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.SkyBox;
import de.simagdo.engine.items.Terrain;
import de.simagdo.engine.loaders.obj.OBJLoader;
import de.simagdo.engine.sound.SoundBuffer;
import de.simagdo.engine.sound.SoundListener;
import de.simagdo.engine.sound.SoundManager;
import de.simagdo.engine.sound.SoundSource;
import de.simagdo.engine.window.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.openal.AL11;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

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
    private FlowParticleEmitter particleEmitter;
    private SoundManager soundManager;
    private MouseBoxSelectionDetector selectionDetector;
    private boolean leftButtonPressed;

    private enum Sounds {
        MUSIC,
        BEEP,
        FIRE
    }

    public DummyGame() {
        this.renderer = new Renderer();
        this.camera = new Camera();
        this.cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        this.angleInc = 0;
        this.lightAngle = 45;
        this.hud = new Hud();
        this.soundManager = new SoundManager();
        this.selectionDetector = new MouseBoxSelectionDetector();
        this.leftButtonPressed = false;
    }

    @Override
    public void init(Window window) throws Exception {
        this.renderer.init(window);

        this.hud.init(window);
        this.scene = new Scene();
        this.soundManager.init();

        float reflectance = 1f;

        float blockScale = 0.5f;
        float skyBoxScale = 100.0f;
        float extension = 2.0f;

        float startx = extension * (-skyBoxScale + blockScale);
        float startz = extension * (skyBoxScale - blockScale);
        float starty = -1.0f;
        float inc = blockScale * 2;

        float posx = startx;
        float posz = startz;
        float incy = 0.0f;

        ByteBuffer buf;
        int width;
        int height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            URL url = Texture.class.getResource("/textures/heightmap.png");
            File file = Paths.get(url.toURI()).toFile();
            String filePath = file.getAbsolutePath();
            buf = stbi_load(filePath, w, h, channels, 4);
            if (buf == null) {
                throw new Exception("Image file not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        int instances = height * width;
        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj", instances);
        mesh.setBoundingRadius(1);
        Texture texture = new Texture("/textures/terrain_textures.png", 2, 1);
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);
        this.gameItems = new GameItem[instances];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                GameItem gameItem = new GameItem(mesh);
                gameItem.setScale(blockScale);
                int rgb = HeightMapMesh.getRGB(i, j, width, buf);
                incy = rgb / (10 * 255 * 255);
                gameItem.setPosition(posx, starty + incy, posz);
                int textPos = Math.random() > 0.5f ? 0 : 1;
                gameItem.setTextPos(textPos);
                gameItems[i * width + j] = gameItem;

                posx += inc;
            }
            posx = startx;
            posz -= inc;
        }
        this.scene.setGameItems(gameItems);

        // Particles
        int maxParticles = 200;
        Vector3f particleSpeed = new Vector3f(0, 1, 0);
        particleSpeed.mul(2.5f);
        long ttl = 4000;
        long creationPeriodMillis = 300;
        float range = 0.2f;
        float scale = 1.0f;
        Mesh partMesh = OBJLoader.loadMesh("/models/particle.obj", maxParticles);
        Texture particleTexture = new Texture("/textures/particle_anim.png", 4, 4);
        Material partMaterial = new Material(particleTexture, reflectance);
        partMesh.setMaterial(partMaterial);
        Particle particle = new Particle(partMesh, particleSpeed, ttl, 100);
        particle.setScale(scale);
        this.particleEmitter = new FlowParticleEmitter(particle, maxParticles, creationPeriodMillis);
        this.particleEmitter.setActive(true);
        this.particleEmitter.setPositionRndRange(range);
        this.particleEmitter.setSpeedRndRange(range);
        this.particleEmitter.setAnimRange(10);
        this.scene.setParticleEmitters(new FlowParticleEmitter[]{this.particleEmitter});

        // Shadows
        this.scene.setRenderShadows(false);

        // Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
        this.scene.setFog(new Fog(true, fogColour, 0.02f));

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox("/models/skybox.obj", new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
        skyBox.setScale(skyBoxScale);
        this.scene.setSkyBox(skyBox);

        //Sounds
        this.soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
        this.setupSounds();

        //Setup Lights
        this.setupLights();

        this.camera.getPosition().x = 0.25f;
        this.camera.getPosition().y = 6.5f;
        this.camera.getPosition().z = 6.5f;
        this.camera.getRotation().x = 25;
        this.camera.getRotation().y = -1;

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

    private void setupSounds() throws Exception {

        SoundBuffer buffBack = new SoundBuffer("/sounds/background.ogg");
        this.soundManager.addSoundBuffer(buffBack);
        SoundSource sourceBack = new SoundSource(true, true);
        sourceBack.setBuffer(buffBack.getBufferId());
        this.soundManager.addSoundSource(Sounds.MUSIC.toString(), sourceBack);

        SoundBuffer buffBeep = new SoundBuffer("/sounds/beep.ogg");
        this.soundManager.addSoundBuffer(buffBeep);
        SoundSource sourceBeep = new SoundSource(false, true);
        sourceBeep.setBuffer(buffBeep.getBufferId());
        this.soundManager.addSoundSource(Sounds.BEEP.toString(), sourceBeep);

        SoundBuffer buffFire = new SoundBuffer("/sounds/fire.ogg");
        this.soundManager.addSoundBuffer(buffFire);
        SoundSource sourceFire = new SoundSource(true, false);
        Vector3f pos = particleEmitter.getBaseParticle().getPosition();
        sourceFire.setPosition(pos);
        sourceFire.setBuffer(buffFire.getBufferId());
        this.soundManager.addSoundSource(Sounds.FIRE.toString(), sourceFire);
        sourceFire.play();

        this.soundManager.setListener(new SoundListener(new Vector3f(0, 0, 0)));

        sourceBack.play();

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
            this.soundManager.playSoundSource(Sounds.BEEP.toString());
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            this.angleInc += 0.05f;
            this.soundManager.playSoundSource(Sounds.FIRE.toString());
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

        this.particleEmitter.update((long) (interval * 1000));

        //Update View Matrix
        this.camera.updateViewMatrix();

        //Update sound listener position
        this.soundManager.updateListenerPosition(camera);

        if (mouseInput.isLeftButtonPressed())
            this.selectionDetector.selectGameItem(this.gameItems, window, mouseInput.getCurrentPos(), this.camera);

        boolean aux = mouseInput.isLeftButtonPressed();
        if (aux && !this.leftButtonPressed && this.selectionDetector.selectGameItem(gameItems, window, mouseInput.getCurrentPos(), camera)) {
            this.hud.incCounter();
        }
        this.leftButtonPressed = aux;
    }

    @Override
    public void render(Window window) {
        this.renderer.render(window, this.camera, this.scene);
        this.hud.render(window);
    }

    @Override
    public void cleanUp() {
        this.renderer.cleanUp();
        this.soundManager.cleanup();
        for (Mesh mesh : scene.getMeshMap().keySet()) mesh.cleanUp();
        if (this.hud != null)
            this.hud.cleanUp();
    }

}
