package de.simagdo.engine.graph;

import de.simagdo.engine.*;
import de.simagdo.engine.graph.animation.AnimatedFrame;
import de.simagdo.engine.graph.animation.AnimatedGameItem;
import de.simagdo.engine.graph.camera.Camera;
import de.simagdo.engine.graph.lights.DirectionalLight;
import de.simagdo.engine.graph.lights.OrthoCoords;
import de.simagdo.engine.graph.lights.PointLight;
import de.simagdo.engine.graph.lights.SpotLight;
import de.simagdo.engine.graph.particles.IParticleEmitter;
import de.simagdo.engine.graph.shadow.ShadowCascade;
import de.simagdo.engine.graph.shadow.ShadowRenderer;
import de.simagdo.engine.graph.text.Texture;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.SkyBox;
import de.simagdo.engine.window.Window;
import de.simagdo.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class Renderer {

    private ShaderProgram sceneShaderProgram;
    private ShaderProgram skyBoxShaderProgram;
    private ShaderProgram particlesShaderProgram;
    private Transformation transformation;
    private float specularPower;
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private final FrustumCullingFilter frustumCullingFilter;
    private final List<GameItem> filteredItems;
    private final ShadowRenderer shadowRenderer;

    public Renderer() {
        this.transformation = new Transformation();
        this.specularPower = 10f;
        this.frustumCullingFilter = new FrustumCullingFilter();
        this.filteredItems = new ArrayList<>();
        this.shadowRenderer = new ShadowRenderer();
    }

    public void init(Window window) throws Exception {
        this.shadowRenderer.init(window);
        this.setupSceneShader();
        this.setupSkyBoxShader();
        this.setupParticlesShader();
    }

    private void setupSceneShader() throws Exception {
        // Create shader
        this.sceneShaderProgram = new ShaderProgram();
        this.sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_shaders/scene_vertex.vs"));
        this.sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_shaders/scene_fragment.fs"));
        this.sceneShaderProgram.link();

        // Create uniforms for View and projection matrices and text
        this.sceneShaderProgram.createUniform("viewMatrix");
        this.sceneShaderProgram.createUniform("projectionMatrix");
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
        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            this.sceneShaderProgram.createUniform("shadowMap_" + i);
        }
        this.sceneShaderProgram.createUniform("orthoProjectionMatrix", ShadowRenderer.NUM_CASCADES);
        this.sceneShaderProgram.createUniform("modelNonInstancedMatrix");
        this.sceneShaderProgram.createUniform("lightViewMatrix", ShadowRenderer.NUM_CASCADES);
        this.sceneShaderProgram.createUniform("cascadeFarPlanes", ShadowRenderer.NUM_CASCADES);
        this.sceneShaderProgram.createUniform("renderShadow");

        //Create uniform for joint Matrices
        this.sceneShaderProgram.createUniform("jointsMatrix");

        this.sceneShaderProgram.createUniform("isInstanced");
        this.sceneShaderProgram.createUniform("numCols");
        this.sceneShaderProgram.createUniform("numRows");

        this.sceneShaderProgram.createUniform("selectedNonInstanced");

    }

    private void setupSkyBoxShader() throws Exception {
        this.skyBoxShaderProgram = new ShaderProgram();
        this.skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/skybox_shaders/sb_vertex.vs"));
        this.skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/skybox_shaders/sb_fragment.fs"));
        this.skyBoxShaderProgram.link();

        this.skyBoxShaderProgram.createUniform("projectionMatrix");
        this.skyBoxShaderProgram.createUniform("modelViewMatrix");
        this.skyBoxShaderProgram.createUniform("texture_sampler");
        this.skyBoxShaderProgram.createUniform("ambientLight");
        this.skyBoxShaderProgram.createUniform("colour");
        this.skyBoxShaderProgram.createUniform("hasTexture");
    }

    private void setupParticlesShader() throws Exception {
        this.particlesShaderProgram = new ShaderProgram();
        this.particlesShaderProgram.createVertexShader(Utils.loadResource("/shaders/particle_shaders/particles_vertex.vs"));
        this.particlesShaderProgram.createFragmentShader(Utils.loadResource("/shaders/particle_shaders/particles_fragment.fs"));
        this.particlesShaderProgram.link();

        this.particlesShaderProgram.createUniform("viewMatrix");
        this.particlesShaderProgram.createUniform("projectionMatrix");
        this.particlesShaderProgram.createUniform("texture_sampler");

        this.particlesShaderProgram.createUniform("numCols");
        this.particlesShaderProgram.createUniform("numRows");
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, Scene scene, boolean sceneChanged) {
        this.clear();

        if (window.getWindowOptions().frustumCulling) {
            this.frustumCullingFilter.updateFrustum(window.getProjectionMatrix(), camera.getViewMatrix());
            this.frustumCullingFilter.filter(scene.getMeshMap());
            this.frustumCullingFilter.filter(scene.getInstancedMeshMap());
        }

        //Render Depth Map before View Ports have been set up
        if (scene.isRenderShadows() && sceneChanged)
            this.shadowRenderer.render(window, scene, camera, this.transformation, this);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        //Update Projection and View Atrices once per render cycle
        window.updateProjectionMatrix();

        //Render Scene
        this.renderScene(window, camera, scene);

        //Render SkyBox
        this.renderSkyBox(window, camera, scene);

        //Render Particles
        this.renderParticles(window, camera, scene);

        //Render Cross Hair
        this.renderCrossHair(window);
    }

    private void renderScene(Window window, Camera camera, Scene scene) {
        this.sceneShaderProgram.bind();

        //Update Projection Matrix
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        this.sceneShaderProgram.setUniform("viewMatrix", viewMatrix);
        this.sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        int i = 0;
        for (ShadowCascade shadowCascade : this.shadowRenderer.getShadowCascades()) {
            this.sceneShaderProgram.setUniform("orthoProjectionMatrix", shadowCascade.getOrthoProjMatrix(), i);
            this.sceneShaderProgram.setUniform("cascadeFarPlanes", ShadowRenderer.CASCADE_SPLITS[i], i);
            this.sceneShaderProgram.setUniform("lightViewMatrix", shadowCascade.getLightViewMatrix(), i);
            i++;
        }

        //Render Lights
        this.renderLights(viewMatrix, scene.getSceneLight());

        this.sceneShaderProgram.setUniform("fog", scene.getFog());
        this.sceneShaderProgram.setUniform("texture_sampler", 0);
        this.sceneShaderProgram.setUniform("normalMap", 1);

        int start = 2;
        for (i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            this.sceneShaderProgram.setUniform("shadowMap_" + i, start + i);
        }

        this.sceneShaderProgram.setUniform("renderShadow", scene.isRenderShadows() ? 1 : 0);

        this.renderNonInstancedMeshes(scene);
        this.renderInstancedMeshes(scene, viewMatrix);

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

    private void renderSkyBox(Window window, Camera camera, Scene scene) {
        SkyBox skyBox = scene.getSkyBox();

        if (skyBox != null) {
            this.skyBoxShaderProgram.bind();

            this.skyBoxShaderProgram.setUniform("texture_sampler", 0);

            //Update projection Matrix
            Matrix4f projectionMatrix = window.getProjectionMatrix();
            this.sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

            Matrix4f viewMatrix = camera.getViewMatrix();
            float m30 = viewMatrix.m30();
            viewMatrix.m30(0);
            float m31 = viewMatrix.m31();
            viewMatrix.m31(0);
            float m32 = viewMatrix.m32();
            viewMatrix.m32(0);

            Mesh mesh = skyBox.getMesh();
            Matrix4f modelViewMatrix = this.transformation.buildModelViewMatrix(skyBox, viewMatrix);
            this.skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            this.skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLight().getAmbientLight());
            this.skyBoxShaderProgram.setUniform("colour", mesh.getMaterial().getAmbientColour());
            this.skyBoxShaderProgram.setUniform("hasTexture", mesh.getMaterial().isTextured() ? 1 : 0);
            mesh.render();

            scene.getSkyBox().getMesh().render();

            viewMatrix.m30(m30);
            viewMatrix.m31(m31);
            viewMatrix.m32(m32);

            this.skyBoxShaderProgram.unbind();
        }
    }

    private void renderParticles(Window window, Camera camera, Scene scene) {
        this.particlesShaderProgram.bind();

        Matrix4f viewMatrix = camera.getViewMatrix();
        this.particlesShaderProgram.setUniform("viewMatrix", viewMatrix);
        this.particlesShaderProgram.setUniform("texture_sampler", 0);
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        this.particlesShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        IParticleEmitter[] emitters = scene.getParticleEmitters();
        int numEmitters = emitters != null ? emitters.length : 0;

        glDepthMask(false);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

        for (int i = 0; i < numEmitters; i++) {
            IParticleEmitter emitter = emitters[i];
            InstancedMesh mesh = (InstancedMesh) emitter.getBaseParticle().getMesh();

            Texture texture = mesh.getMaterial().getTexture();
            this.particlesShaderProgram.setUniform("numCols", texture.getNumCols());
            this.particlesShaderProgram.setUniform("numRows", texture.getNumRows());

            mesh.renderListInstanced(emitter.getParticles(), true, this.transformation, viewMatrix);

        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);

        this.particlesShaderProgram.unbind();
    }

    private void renderNonInstancedMeshes(Scene scene) {
        this.sceneShaderProgram.setUniform("isInstanced", 0);

        // Render each mesh with the associated game Items
        Map<Mesh, List<GameItem>> mapMeshes = scene.getMeshMap();
        for (Mesh mesh : mapMeshes.keySet()) {

            this.sceneShaderProgram.setUniform("material",mesh.getMaterial());

            Texture text = mesh.getMaterial() != null ? mesh.getMaterial().getTexture() : null;
            if (text != null) {
                sceneShaderProgram.setUniform("numCols", text.getNumCols());
                sceneShaderProgram.setUniform("numRows", text.getNumRows());
            }

            this.shadowRenderer.bindTextures(GL_TEXTURE2);

            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                        this.sceneShaderProgram.setUniform("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);
                        Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
                        this.sceneShaderProgram.setUniform("modelNonInstancedMatrix", modelMatrix);

                        if (gameItem instanceof AnimatedGameItem) {
                            AnimatedGameItem animGameItem = (AnimatedGameItem) gameItem;
                            AnimatedFrame frame = animGameItem.getCurrentFrame();
                            this.sceneShaderProgram.setUniform("jointsMatrix", frame.getJointMatrices());
                        }
                    }
            );
        }
    }

    private void renderInstancedMeshes(Scene scene, Matrix4f viewMatrix) {
        this.sceneShaderProgram.setUniform("isInstanced", 1);

        // Render each mesh with the associated game Items
        Map<InstancedMesh, List<GameItem>> mapMeshes = scene.getInstancedMeshMap();
        for (InstancedMesh mesh : mapMeshes.keySet()) {
            Texture text = mesh.getMaterial().getTexture();
            if (text != null) {
                sceneShaderProgram.setUniform("numCols", text.getNumCols());
                sceneShaderProgram.setUniform("numRows", text.getNumRows());
            }

            this.sceneShaderProgram.setUniform("material", mesh.getMaterial());

            this.filteredItems.clear();
            for (GameItem gameItem : mapMeshes.get(mesh)) {
                if (gameItem.isInsideFrustum()) {
                    this.filteredItems.add(gameItem);
                }
            }

            this.shadowRenderer.bindTextures(GL_TEXTURE2);

            mesh.renderListInstanced(mapMeshes.get(mesh), transformation, viewMatrix);
        }
    }

    private void renderCrossHair(Window window) {
        if (window.getWindowOptions().compatibleProfile) {
            glPushMatrix();
            glLoadIdentity();

            float inc = 0.05f;
            glLineWidth(2.0f);

            glBegin(GL_LINES);

            glColor3f(1.0f, 1.0f, 1.0f);

            //Horizontal Line
            glVertex3f(-inc, 0.0f, 0.0f);
            glVertex3f(+inc, 0.0f, 0.0f);
            glEnd();

            //Vertical Line
            glBegin(GL_LINES);
            glVertex3f(0.0f, -inc, 0.0f);
            glVertex3f(0.0f, +inc, 0.0f);
            glEnd();

            glPopMatrix();

        }
    }

    /**
     * Renders the three axis in space (For debugging purposes only
     *
     * @param camera
     */
    private void renderAxes(Window window, Camera camera) {
        if (window.getWindowOptions().compatibleProfile) {
            glPushMatrix();
            glLoadIdentity();
            float rotX = camera.getRotation().x;
            float rotY = camera.getRotation().y;
            float rotZ = 0;
            glRotatef(rotX, 1.0f, 0.0f, 0.0f);
            glRotatef(rotY, 0.0f, 1.0f, 0.0f);
            glRotatef(rotZ, 0.0f, 0.0f, 1.0f);
            glLineWidth(2.0f);

            glBegin(GL_LINES);
            // X Axis
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 0.0f, 0.0f);
            glVertex3f(1.0f, 0.0f, 0.0f);
            // Y Axis
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(0.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 1.0f, 0.0f);
            // Z Axis
            glColor3f(1.0f, 1.0f, 1.0f);
            glVertex3f(0.0f, 0.0f, 0.0f);
            glVertex3f(0.0f, 0.0f, 1.0f);
            glEnd();

            glPopMatrix();
        }
    }

    public void cleanUp() {
        if (this.sceneShaderProgram != null) this.sceneShaderProgram.cleanUp();
        if (this.skyBoxShaderProgram != null) this.skyBoxShaderProgram.cleanUp();
        if (this.shadowRenderer != null) this.shadowRenderer.cleanUp();
        if (this.particlesShaderProgram != null) this.particlesShaderProgram.cleanUp();
    }

}