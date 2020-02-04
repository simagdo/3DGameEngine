package de.simagdo.engine.graph;

import de.simagdo.engine.*;
import de.simagdo.engine.graph.animation.AnimatedFrame;
import de.simagdo.engine.graph.animation.AnimatedGameItem;
import de.simagdo.engine.graph.lights.DirectionalLight;
import de.simagdo.engine.graph.lights.OrthoCoords;
import de.simagdo.engine.graph.lights.PointLight;
import de.simagdo.engine.graph.lights.SpotLight;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.SkyBox;
import de.simagdo.engine.window.Window;
import de.simagdo.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Renderer {

    private ShaderProgram sceneShaderProgram;
    private ShaderProgram hudShaderProgram;
    private ShaderProgram skyBoxShaderProgram;
    private ShaderProgram depthShaderProgram;
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private Transformation transformation;
    private float specularPower;
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private ShadowMap shadowMap;

    public Renderer() {
        this.transformation = new Transformation();
        this.specularPower = 10f;
    }

    public void init(Window window) throws Exception {
        this.shadowMap = new ShadowMap();
        this.setupDepthShader();
        this.setupSceneShader();
        this.setupHudShader();
        this.setupSkyBoxShader();
    }

    private void setupSceneShader() throws Exception {
        // Create shader
        this.sceneShaderProgram = new ShaderProgram();
        this.sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_vertex.vs"));
        this.sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_fragment.fs"));
        this.sceneShaderProgram.link();

        // Create uniforms for modelView and projection matrices and text
        this.sceneShaderProgram.createUniform("projectionMatrix");
        this.sceneShaderProgram.createUniform("modelViewMatrix");
        this.sceneShaderProgram.createUniform("texture_sampler");
        this.sceneShaderProgram.createUniform("normalMap");

        //Create uniform for material
        this.sceneShaderProgram.createMaterialUniform("material");

        // Create lighting related uniforms
        this.sceneShaderProgram.createUniform("specularPower");
        this.sceneShaderProgram.createUniform("ambientLight");
        this.sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        this.sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        this.sceneShaderProgram.createDirectionalLightUniform("directionalLight");
        this.sceneShaderProgram.createFogUniform("fog");

        //Create uniforms for Shadow mapping
        this.sceneShaderProgram.createUniform("shadowMap");
        this.sceneShaderProgram.createUniform("orthoProjectionMatrix");
        this.sceneShaderProgram.createUniform("modelLightViewMatrix");

        //Create uniform for joint Matrices
        this.sceneShaderProgram.createUniform("jointsMatrix");

    }

    private void setupHudShader() throws Exception {
        this.hudShaderProgram = new ShaderProgram();
        this.hudShaderProgram.createVertexShader(Utils.loadResource("/shaders/hud_vertex.vs"));
        this.hudShaderProgram.createFragmentShader(Utils.loadResource("/shaders/hud_fragment.fs"));
        this.hudShaderProgram.link();

        //Create uniforms for Ortographic-model projection matrix and base colour
        this.hudShaderProgram.createUniform("projModelMatrix");
        this.hudShaderProgram.createUniform("colour");
        this.hudShaderProgram.createUniform("hasTexture");
    }

    private void setupSkyBoxShader() throws Exception {
        this.skyBoxShaderProgram = new ShaderProgram();
        this.skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/sb_vertex.vs"));
        this.skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/sb_fragment.fs"));
        this.skyBoxShaderProgram.link();

        this.skyBoxShaderProgram.createUniform("projectionMatrix");
        this.skyBoxShaderProgram.createUniform("modelViewMatrix");
        this.skyBoxShaderProgram.createUniform("texture_sampler");
        this.skyBoxShaderProgram.createUniform("ambientLight");
    }

    private void setupDepthShader() throws Exception {
        this.depthShaderProgram = new ShaderProgram();
        this.depthShaderProgram.createVertexShader(Utils.loadResource("/shaders/depth_vertex.vs"));
        this.depthShaderProgram.createFragmentShader(Utils.loadResource("/shaders/depth_fragment.fs"));
        this.depthShaderProgram.link();

        this.depthShaderProgram.createUniform("orthoProjectionMatrix");
        this.depthShaderProgram.createUniform("modelLightViewMatrix");

        //Create uniform for joint matrices
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, Scene scene, IHud hud) {
        this.clear();

        //Render Depth Map before View Ports have been set up
        this.renderDepthMap(window, camera, scene);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        //Update Projection and View Atrices once per render cycle
        this.transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        this.transformation.updateViewMatrix(camera);

        //Render Scene
        this.renderScene(window, camera, scene);

        //Render SkyBox
        this.renderSkyBox(window, camera, scene);

        //Render HUD
        this.renderHud(window, hud);
    }

    private void renderScene(Window window, Camera camera, Scene scene) {
        this.sceneShaderProgram.bind();

        //Update Projection Matrix
        Matrix4f projectionMatrix = this.transformation.getProjectionMatrix();
        this.sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f orthoProjectionMatrix = this.transformation.getOrthoProjectionMatrix();
        this.sceneShaderProgram.setUniform("orthoProjectionMatrix", orthoProjectionMatrix);
        Matrix4f lightViewMatrix = this.transformation.getLightViewMatrix();

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix();

        //Update Light Uniforms
        this.renderLights(viewMatrix, scene.getSceneLight());

        this.sceneShaderProgram.setUniform("fog", scene.getFog());
        this.sceneShaderProgram.setUniform("texture_sampler", 0);
        this.sceneShaderProgram.setUniform("normalMap", 1);
        this.sceneShaderProgram.setUniform("shadowMap", 2);

        // Render each gameItem
        for (Mesh mesh : scene.getMeshMap().keySet()) {
            this.sceneShaderProgram.setUniform("material", mesh.getMaterial());
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, this.shadowMap.getDepthMap().getId());
            mesh.renderList(scene.getMeshMap().get(mesh), (GameItem gameItem) -> {
                Matrix4f modelViewMatrix = this.transformation.buildModelViewMatrix(gameItem, viewMatrix);
                this.sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                Matrix4f modelLightViewMatrix = this.transformation.buildModelLightViewMatrix(gameItem, lightViewMatrix);
                this.sceneShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);

                if (gameItem instanceof AnimatedGameItem) {
                    AnimatedGameItem animatedGameItem = (AnimatedGameItem) gameItem;
                    AnimatedFrame frame = animatedGameItem.getCurrentFrame();
                    this.sceneShaderProgram.setUniform("jointsMatrix", frame.getJointMatrices());
                }

            });
        }

        this.sceneShaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {
        this.sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        this.sceneShaderProgram.setUniform("specularPower", this.specularPower);

        //Process Point Lights
        if (sceneLight.getPointLights() != null) {
            for (int i = 0; i < sceneLight.getPointLights().length; i++) {
                // Get a copy of the point lights object and transform its position to view coordinates
                PointLight currPointLight = new PointLight(sceneLight.getPointLights()[i]);
                Vector3f lightPos = currPointLight.getPosition();
                Vector4f aux = new Vector4f(lightPos, 1);
                aux.mul(viewMatrix);
                lightPos.x = aux.x;
                lightPos.y = aux.y;
                lightPos.z = aux.z;
                this.sceneShaderProgram.setUniform("pointLights", currPointLight, i);
            }
        }

        //Process Spot Lights
        if (sceneLight.getSpotLights() != null) {
            for (int i = 0; i < sceneLight.getSpotLights().length; i++) {
                // Get a copy of the spot lights object and transform its position and cone direction to view coordinates
                SpotLight currSpotLight = new SpotLight(sceneLight.getSpotLights()[i]);
                Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
                dir.mul(viewMatrix);
                currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
                Vector3f lightPos = currSpotLight.getPointLight().getPosition();

                Vector4f aux = new Vector4f(lightPos, 1);
                aux.mul(viewMatrix);
                lightPos.x = aux.x;
                lightPos.y = aux.y;
                lightPos.z = aux.z;

                this.sceneShaderProgram.setUniform("spotLights", currSpotLight, i);
            }
        }

        //Get a copy of the directional lights object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        this.sceneShaderProgram.setUniform("directionalLight", sceneLight.getDirectionalLight());

    }

    private void renderHud(Window window, IHud hud) {
        if (hud != null) {
            this.hudShaderProgram.bind();

            Matrix4f ortho = this.transformation.getOrtho2DProjectionMatrix(0, window.getWidth(), window.getHeight(), 0);

            for (GameItem gameItem : hud.getGameItems()) {
                Mesh mesh = gameItem.getMesh();

                //Set ortotaphic and model matrix for this HUD item
                Matrix4f projModelMatrix = this.transformation.buildOrtoProjModelMatrix(gameItem, ortho);
                this.hudShaderProgram.setUniform("projModelMatrix", projModelMatrix);
                this.hudShaderProgram.setUniform("colour", gameItem.getMesh().getMaterial().getAmbientColour());
                this.hudShaderProgram.setUniform("hasTexture", gameItem.getMesh().getMaterial().isTextured() ? 1 : 0);

                //Render the Mesh for this HUD Item
                mesh.render();
            }

            this.hudShaderProgram.unbind();
        }
    }

    private void renderSkyBox(Window window, Camera camera, Scene scene) {
        SkyBox skyBox = scene.getSkyBox();

        if (skyBox != null) {
            this.skyBoxShaderProgram.bind();

            this.skyBoxShaderProgram.setUniform("texture_sampler", 0);

            //Update projection Matrix
            Matrix4f projectionMatrix = this.transformation.getProjectionMatrix();
            this.sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

            Matrix4f viewMatrix = this.transformation.getViewMatrix();
            viewMatrix.m30(0);
            viewMatrix.m31(0);
            viewMatrix.m32(0);
            Matrix4f modelViewMatrix = this.transformation.buildModelViewMatrix(skyBox, viewMatrix);
            this.skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            this.skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLight().getAmbientLight());

            scene.getSkyBox().getMesh().render();

            this.skyBoxShaderProgram.unbind();
        }
    }

    private void renderDepthMap(Window window, Camera camera, Scene scene) {
        //Setup View Port to match the Texture size
        glBindFramebuffer(GL_FRAMEBUFFER, this.shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        this.depthShaderProgram.bind();

        DirectionalLight directionalLight = scene.getSceneLight().getDirectionalLight();
        Vector3f lightDirection = directionalLight.getDirection();

        float lightAngleX = (float) Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float) Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;

        Matrix4f lightViewMatrix = this.transformation.updateLightViewMatrix(
                new Vector3f(lightDirection).mul(directionalLight.getShadowPosMult()),
                new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        OrthoCoords orthoCoords = directionalLight.getOrthoCoords();
        Matrix4f orthoProjectionMatrix = this.transformation.updateOrthoProjectionMatrix(orthoCoords.left, orthoCoords.right, orthoCoords.bottom, orthoCoords.top, orthoCoords.near, orthoCoords.far);
        this.depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjectionMatrix);

        scene.getMeshMap().forEach((mesh, gameItems) -> {
            mesh.renderList(gameItems, gameItem -> {
                Matrix4f modelLightViewMatrix = this.transformation.buildModelViewMatrix(gameItem, lightViewMatrix);
                this.depthShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);

                if (gameItem instanceof AnimatedGameItem) {
                    AnimatedGameItem animatedGameItem = (AnimatedGameItem) gameItem;
                    AnimatedFrame frame = animatedGameItem.getCurrentFrame();
                    this.sceneShaderProgram.setUniform("jointsMatrix", frame.getJointMatrices());
                }

            });
        });

        //Unbind
        this.depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public void cleanUp() {
        if (this.sceneShaderProgram != null) this.sceneShaderProgram.cleanUp();
        if (this.hudShaderProgram != null) this.hudShaderProgram.cleanUp();
        if (this.skyBoxShaderProgram != null) this.skyBoxShaderProgram.cleanUp();
        if (this.shadowMap != null) this.shadowMap.cleanUp();
    }

}