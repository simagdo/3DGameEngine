package de.simagdo.engine.graph;

import de.simagdo.engine.Scene;
import de.simagdo.engine.SceneLight;
import de.simagdo.engine.graph.animation.AnimatedFrame;
import de.simagdo.engine.graph.animation.AnimatedGameItem;
import de.simagdo.engine.graph.camera.Camera;
import de.simagdo.engine.graph.lights.DirectionalLight;
import de.simagdo.engine.graph.lights.PointLight;
import de.simagdo.engine.graph.particles.IParticleEmitter;
import de.simagdo.engine.graph.shadow.ShadowCascade;
import de.simagdo.engine.graph.shadow.ShadowRenderer;
import de.simagdo.engine.graph.text.Texture;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.SkyBox;
import de.simagdo.engine.loaders.assimp.StaticMeshesLoader;
import de.simagdo.engine.window.Window;
import de.simagdo.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    private final Transformation transformation;
    private final ShadowRenderer shadowRenderer;
    private ShaderProgram skyBoxShaderProgram;
    private ShaderProgram particlesShaderProgram;
    private ShaderProgram gBufferShaderProgram;
    private ShaderProgram dirLightShaderProgram;
    private ShaderProgram pointLightShaderProgram;
    private ShaderProgram fogShaderProgram;
    private final float specularPower;
    private final FrustumCullingFilter frustumFilter;
    private final List<GameItem> filteredItems;
    private GBuffer gBuffer;
    private SceneBuffer sceneBuffer;
    private Mesh bufferPassMesh;
    private Matrix4f bufferPassModelMatrix;
    private Vector4f tmpVec;

    public Renderer() {
        this.transformation = new Transformation();
        this.specularPower = 10f;
        this.shadowRenderer = new ShadowRenderer();
        this.frustumFilter = new FrustumCullingFilter();
        this.filteredItems = new ArrayList<>();
        this.tmpVec = new Vector4f();
    }

    public void init(Window window) throws Exception {
        this.shadowRenderer.init(window);
        this.gBuffer = new GBuffer(window);
        this.sceneBuffer = new SceneBuffer(window);
        this.setupSkyBoxShader();
        this.setupParticlesShader();
        this.setupGeometryShader();
        this.setupDirLightShader();
        this.setupPointLightShader();
        this.setupFogShader();

        this.bufferPassModelMatrix =  new Matrix4f();
        this.bufferPassMesh = StaticMeshesLoader.load("models/buffer_pass_mess.obj", "/models")[0];
    }

    public void render(Window window, Camera camera, Scene scene, boolean sceneChanged) {
        this.clear();

        if (window.getWindowOptions().frustumCulling) {
            this.frustumFilter.updateFrustum(window.getProjectionMatrix(), camera.getViewMatrix());
            this.frustumFilter.filter(scene.getMeshMap());
            this.frustumFilter.filter(scene.getInstancedMeshMap());
        }

        // Render depth map before view ports has been set up
        if (scene.isRenderShadows() && sceneChanged) {
            this.shadowRenderer.render(window, scene, camera, this.transformation, this);
        }

        glViewport(0, 0, window.getWidth(), window.getHeight());

        // Update projection matrix once per render cycle
        window.updateProjectionMatrix();

        this.renderGeometry(window, camera, scene);

        this.initLightRendering();
        this.renderPointLights(window, camera, scene);
        this.renderDirectionalLight(window, camera, scene);
        this.endLightRendering();

        this.renderFog(window, camera, scene);
        this.renderSkyBox(window, camera, scene);
        this.renderParticles(window, camera, scene);
    }

    private void setupParticlesShader() throws Exception {
        this.particlesShaderProgram = new ShaderProgram();
        this.particlesShaderProgram.createVertexShader(Utils.loadResource("/shaders/particle/particles_vertex.vs"));
        this.particlesShaderProgram.createFragmentShader(Utils.loadResource("/shaders/particle/particles_fragment.fs"));
        this.particlesShaderProgram.link();

        this.particlesShaderProgram.createUniform("viewMatrix");
        this.particlesShaderProgram.createUniform("projectionMatrix");
        this.particlesShaderProgram.createUniform("texture_sampler");

        this.particlesShaderProgram.createUniform("numCols");
        this.particlesShaderProgram.createUniform("numRows");
    }

    private void setupSkyBoxShader() throws Exception {
        this.skyBoxShaderProgram = new ShaderProgram();
        this.skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/skyBox/sb_vertex.vs"));
        this.skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/skyBox/sb_fragment.fs"));
        this.skyBoxShaderProgram.link();

        // Create uniforms for projection matrix
        this.skyBoxShaderProgram.createUniform("projectionMatrix");
        this.skyBoxShaderProgram.createUniform("modelViewMatrix");
        this.skyBoxShaderProgram.createUniform("texture_sampler");
        this.skyBoxShaderProgram.createUniform("ambientLight");
        this.skyBoxShaderProgram.createUniform("colour");
        this.skyBoxShaderProgram.createUniform("hasTexture");

        this.skyBoxShaderProgram.createUniform("depthsText");
        this.skyBoxShaderProgram.createUniform("screenSize");
    }

    private void setupGeometryShader() throws Exception {
        this.gBufferShaderProgram = new ShaderProgram();
        this.gBufferShaderProgram.createVertexShader(Utils.loadResource("/shaders/gbuffer/gbuffer_vertex.vs"));
        this.gBufferShaderProgram.createFragmentShader(Utils.loadResource("/shaders/gbuffer/gbuffer_fragment.fs"));
        this.gBufferShaderProgram.link();

        this.gBufferShaderProgram.createUniform("projectionMatrix");
        this.gBufferShaderProgram.createUniform("viewMatrix");
        this.gBufferShaderProgram.createUniform("texture_sampler");
        this.gBufferShaderProgram.createUniform("normalMap");
        this.gBufferShaderProgram.createMaterialUniform("material");
        this.gBufferShaderProgram.createUniform("isInstanced");
        this.gBufferShaderProgram.createUniform("modelNonInstancedMatrix");
        this.gBufferShaderProgram.createUniform("selectedNonInstanced");
        this.gBufferShaderProgram.createUniform("jointsMatrix");
        this.gBufferShaderProgram.createUniform("numCols");
        this.gBufferShaderProgram.createUniform("numRows");

        // Create uniforms for shadow mapping
        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            this.gBufferShaderProgram.createUniform("shadowMap_" + i);
        }
        this.gBufferShaderProgram.createUniform("orthoProjectionMatrix", ShadowRenderer.NUM_CASCADES);
        this.gBufferShaderProgram.createUniform("lightViewMatrix", ShadowRenderer.NUM_CASCADES);
        this.gBufferShaderProgram.createUniform("cascadeFarPlanes", ShadowRenderer.NUM_CASCADES);
        this.gBufferShaderProgram.createUniform("renderShadow");
    }

    private void setupDirLightShader() throws Exception {
        this.dirLightShaderProgram = new ShaderProgram();
        this.dirLightShaderProgram.createVertexShader(Utils.loadResource("/shaders/light/light_vertex.vs"));
        this.dirLightShaderProgram.createFragmentShader(Utils.loadResource("/shaders/dir_light/dir_light_fragment.fs"));
        this.dirLightShaderProgram.link();

        this.dirLightShaderProgram.createUniform("modelMatrix");
        this.dirLightShaderProgram.createUniform("projectionMatrix");

        this.dirLightShaderProgram.createUniform("screenSize");
        this.dirLightShaderProgram.createUniform("positionsText");
        this.dirLightShaderProgram.createUniform("diffuseText");
        this.dirLightShaderProgram.createUniform("specularText");
        this.dirLightShaderProgram.createUniform("normalsText");
        this.dirLightShaderProgram.createUniform("shadowText");

        this.dirLightShaderProgram.createUniform("specularPower");
        this.dirLightShaderProgram.createUniform("ambientLight");
        this.dirLightShaderProgram.createDirectionalLightUniform("directionalLight");
    }

    private void setupPointLightShader() throws Exception {
        this.pointLightShaderProgram = new ShaderProgram();
        this.pointLightShaderProgram.createVertexShader(Utils.loadResource("/shaders/light/light_vertex.vs"));
        this.pointLightShaderProgram.createFragmentShader(Utils.loadResource("/shaders/point_light/point_light_fragment.fs"));
        this.pointLightShaderProgram.link();

        this.pointLightShaderProgram.createUniform("modelMatrix");
        this.pointLightShaderProgram.createUniform("projectionMatrix");

        this.pointLightShaderProgram.createUniform("screenSize");
        this.pointLightShaderProgram.createUniform("positionsText");
        this.pointLightShaderProgram.createUniform("diffuseText");
        this.pointLightShaderProgram.createUniform("specularText");
        this.pointLightShaderProgram.createUniform("normalsText");
        this.pointLightShaderProgram.createUniform("shadowText");

        this.pointLightShaderProgram.createUniform("specularPower");
        this.pointLightShaderProgram.createPointLightUniform("pointLight");
    }

    private void setupFogShader() throws Exception {
        this.fogShaderProgram = new ShaderProgram();
        this.fogShaderProgram.createVertexShader(Utils.loadResource("/shaders/light/light_vertex.vs"));
        this.fogShaderProgram.createFragmentShader(Utils.loadResource("/shaders/fog/fog_fragment.fs"));
        this.fogShaderProgram.link();

        this.fogShaderProgram.createUniform("modelMatrix");
        this.fogShaderProgram.createUniform("viewMatrix");
        this.fogShaderProgram.createUniform("projectionMatrix");

        this.fogShaderProgram.createUniform("screenSize");
        this.fogShaderProgram.createUniform("positionsText");
        this.fogShaderProgram.createUniform("depthText");
        this.fogShaderProgram.createUniform("sceneText");

        this.fogShaderProgram.createFogUniform("fog");
        this.fogShaderProgram.createUniform("ambientLight");
        this.fogShaderProgram.createUniform("lightColour");
        this.fogShaderProgram.createUniform("lightIntensity");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    private void renderGeometry(Window window, Camera camera, Scene scene) {
        // Render G-Buffer for writing
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, this.gBuffer.getGBufferId());

        clear();

        glDisable(GL_BLEND);

        this.gBufferShaderProgram.bind();

        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        this.gBufferShaderProgram.setUniform("viewMatrix", viewMatrix);
        this.gBufferShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        this.gBufferShaderProgram.setUniform("texture_sampler", 0);
        this.gBufferShaderProgram.setUniform("normalMap", 1);

        List<ShadowCascade> shadowCascades = this.shadowRenderer.getShadowCascades();
        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            ShadowCascade shadowCascade = shadowCascades.get(i);
            this.gBufferShaderProgram.setUniform("orthoProjectionMatrix", shadowCascade.getOrthoProjMatrix(), i);
            this.gBufferShaderProgram.setUniform("cascadeFarPlanes", ShadowRenderer.CASCADE_SPLITS[i], i);
            this.gBufferShaderProgram.setUniform("lightViewMatrix", shadowCascade.getLightViewMatrix(), i);
        }
        this.shadowRenderer.bindTextures(GL_TEXTURE2);
        int start = 2;
        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            this.gBufferShaderProgram.setUniform("shadowMap_" + i, start + i);
        }
        this.gBufferShaderProgram.setUniform("renderShadow", scene.isRenderShadows() ? 1 : 0);

        renderNonInstancedMeshes(scene);

        renderInstancedMeshes(scene, viewMatrix);

        this.gBufferShaderProgram.unbind();

        glEnable(GL_BLEND);
    }

    private void initLightRendering() {
        // Bind scene buffer
        glBindFramebuffer(GL_FRAMEBUFFER, this.sceneBuffer.getBufferId());

        // Clear G-Buffer
        clear();

        // Disable depth testing to allow the drawing of multiple layers with the same depth
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_ONE, GL_ONE);

        // Bind GBuffer for reading
        glBindFramebuffer(GL_READ_FRAMEBUFFER, this.gBuffer.getGBufferId());
    }

    private void endLightRendering() {
        // Bind screen for writing
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
    }

    private void renderPointLights(Window window, Camera camera, Scene scene) {
        this.pointLightShaderProgram.bind();

        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        this.pointLightShaderProgram.setUniform("modelMatrix", this.bufferPassModelMatrix);
        this.pointLightShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Specular factor
        this.pointLightShaderProgram.setUniform("specularPower", this.specularPower);

        // Bind the G-Buffer textures
        int[] textureIds = this.gBuffer.getTextureIds();
        int numTextures = textureIds != null ? textureIds.length : 0;
        for (int i=0; i<numTextures; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, textureIds[i]);
        }

        this.pointLightShaderProgram.setUniform("positionsText", 0);
        this.pointLightShaderProgram.setUniform("diffuseText", 1);
        this.pointLightShaderProgram.setUniform("specularText", 2);
        this.pointLightShaderProgram.setUniform("normalsText", 3);
        this.pointLightShaderProgram.setUniform("shadowText", 4);

        this.pointLightShaderProgram.setUniform("screenSize", (float) this.gBuffer.getWidth(), (float)this.gBuffer.getHeight());

        SceneLight sceneLight = scene.getSceneLight();
        PointLight[] pointLights = sceneLight.getPointLights();
        int numPointLights = pointLights != null ? pointLights.length : 0;
        for(int i=0; i<numPointLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLights[i]);
            Vector3f lightPos = currPointLight.getPosition();
            this.tmpVec.set(lightPos, 1);
            this.tmpVec.mul(viewMatrix);
            lightPos.x = this.tmpVec.x;
            lightPos.y = this.tmpVec.y;
            lightPos.z = this.tmpVec.z;
            this.pointLightShaderProgram.setUniform("pointLight", currPointLight);

            this.bufferPassMesh.render();
        }

        this.pointLightShaderProgram.unbind();
    }

    private void renderDirectionalLight(Window window, Camera camera, Scene scene) {
        this.dirLightShaderProgram.bind();

        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        this.dirLightShaderProgram.setUniform("modelMatrix", this.bufferPassModelMatrix);
        this.dirLightShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Specular factor
        this.dirLightShaderProgram.setUniform("specularPower", this.specularPower);

        // Bind the G-Buffer textures
        int[] textureIds = this.gBuffer.getTextureIds();
        int numTextures = textureIds != null ? textureIds.length : 0;
        for (int i=0; i<numTextures; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, textureIds[i]);
        }

        this.dirLightShaderProgram.setUniform("positionsText", 0);
        this.dirLightShaderProgram.setUniform("diffuseText", 1);
        this.dirLightShaderProgram.setUniform("specularText", 2);
        this.dirLightShaderProgram.setUniform("normalsText", 3);
        this.dirLightShaderProgram.setUniform("shadowText", 4);

        this.dirLightShaderProgram.setUniform("screenSize", (float) this.gBuffer.getWidth(), (float)this.gBuffer.getHeight());

        // Ambient light
        SceneLight sceneLight = scene.getSceneLight();
        this.dirLightShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());

        // Directional light
        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        this.tmpVec.set(currDirLight.getDirection(), 0);
        this.tmpVec.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(this.tmpVec.x, this.tmpVec.y, this.tmpVec.z));
        this.dirLightShaderProgram.setUniform("directionalLight", currDirLight);

        this.bufferPassMesh.render();

        this.dirLightShaderProgram.unbind();
    }

    private void renderFog(Window window, Camera camera, Scene scene) {
        this.fogShaderProgram.bind();

        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        this.fogShaderProgram.setUniform("modelMatrix", this.bufferPassModelMatrix);
        this.fogShaderProgram.setUniform("viewMatrix", viewMatrix);
        this.fogShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Bind the scene buffer texture and the the depth texture of the G-Buffer
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.gBuffer.getPositionTexture());
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, this.gBuffer.getDepthTexture());
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, this.sceneBuffer.getTextureId());

        this.fogShaderProgram.setUniform("positionsText", 0);
        this.fogShaderProgram.setUniform("depthText", 1);
        this.fogShaderProgram.setUniform("sceneText", 2);

        this.fogShaderProgram.setUniform("screenSize", (float) this.gBuffer.getWidth(), (float)this.gBuffer.getHeight());

        this.fogShaderProgram.setUniform("fog", scene.getFog());
        SceneLight sceneLight = scene.getSceneLight();
        this.fogShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        DirectionalLight dirLight = sceneLight.getDirectionalLight();
        this.fogShaderProgram.setUniform("lightColour", dirLight.getColor());
        this.fogShaderProgram.setUniform("lightIntensity", dirLight.getIntensity());

        this.bufferPassMesh.render();

        this.fogShaderProgram.unbind();
    }

    private void renderParticles(Window window, Camera camera, Scene scene) {
        // Support for transparencies
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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

            Texture text = mesh.getMaterial().getTexture();
            this.particlesShaderProgram.setUniform("numCols", text.getNumCols());
            this.particlesShaderProgram.setUniform("numRows", text.getNumRows());

            mesh.renderListInstanced(emitter.getParticles(), true, this.transformation, viewMatrix);
        }

        glDisable(GL_BLEND);
        glDepthMask(true);

        this.particlesShaderProgram.unbind();
    }

    private void renderSkyBox(Window window, Camera camera, Scene scene) {
        SkyBox skyBox = scene.getSkyBox();
        if (skyBox != null) {
            this.skyBoxShaderProgram.bind();

            this.skyBoxShaderProgram.setUniform("texture_sampler", 0);

            Matrix4f projectionMatrix = window.getProjectionMatrix();
            this.skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
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
            this.skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLight().getSkyBoxLight());
            this.skyBoxShaderProgram.setUniform("colour", mesh.getMaterial().getDiffuseColour());
            this.skyBoxShaderProgram.setUniform("hasTexture", mesh.getMaterial().isTextured() ? 1 : 0);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, this.gBuffer.getDepthTexture());
            this.skyBoxShaderProgram.setUniform("screenSize", (float)window.getWidth(), (float)window.getHeight());
            this.skyBoxShaderProgram.setUniform("depthsText", 1);

            mesh.render();

            viewMatrix.m30(m30);
            viewMatrix.m31(m31);
            viewMatrix.m32(m32);
            this.skyBoxShaderProgram.unbind();
        }
    }

    private void renderNonInstancedMeshes(Scene scene) {
        this.gBufferShaderProgram.setUniform("isInstanced", 0);

        // Render each mesh with the associated game Items
        Map<Mesh, List<GameItem>> mapMeshes = scene.getMeshMap();
        for (Mesh mesh : mapMeshes.keySet()) {
            this.gBufferShaderProgram.setUniform("material", mesh.getMaterial());

            Texture text = mesh.getMaterial().getTexture();
            if (text != null) {
                this.gBufferShaderProgram.setUniform("numCols", text.getNumCols());
                this.gBufferShaderProgram.setUniform("numRows", text.getNumRows());
            }

            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                        this.gBufferShaderProgram.setUniform("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);
                        Matrix4f modelMatrix = this.transformation.buildModelMatrix(gameItem);
                        this.gBufferShaderProgram.setUniform("modelNonInstancedMatrix", modelMatrix);
                        if (gameItem instanceof AnimatedGameItem) {
                            AnimatedGameItem animGameItem = (AnimatedGameItem) gameItem;
                            AnimatedFrame frame = animGameItem.getCurrentAnimation().getCurrentFrame();
                            this.gBufferShaderProgram.setUniform("jointsMatrix", frame.getJointMatrices());
                        }
                    }
            );
        }
    }

    private void renderInstancedMeshes(Scene scene, Matrix4f viewMatrix) {
        this.gBufferShaderProgram.setUniform("isInstanced", 1);

        // Render each mesh with the associated game Items
        Map<InstancedMesh, List<GameItem>> mapMeshes = scene.getInstancedMeshMap();
        for (InstancedMesh mesh : mapMeshes.keySet()) {
            Texture text = mesh.getMaterial().getTexture();
            if (text != null) {
                this.gBufferShaderProgram.setUniform("numCols", text.getNumCols());
                this.gBufferShaderProgram.setUniform("numRows", text.getNumRows());
            }

            this.gBufferShaderProgram.setUniform("material", mesh.getMaterial());

            this.filteredItems.clear();
            for (GameItem gameItem : mapMeshes.get(mesh)) {
                if (gameItem.isInsideFrustum()) {
                    this.filteredItems.add(gameItem);
                }
            }

            mesh.renderListInstanced(this.filteredItems, this.transformation, viewMatrix);
        }
    }

    public void cleanUp() {
        if (this.shadowRenderer != null) {
            this.shadowRenderer.cleanUp();
        }
        if (this.skyBoxShaderProgram != null) {
            this.skyBoxShaderProgram.cleanUp();
        }
        if (this.particlesShaderProgram != null) {
            this.particlesShaderProgram.cleanUp();
        }
        if (this.gBufferShaderProgram != null) {
            this.gBufferShaderProgram.cleanUp();
        }
        if (this.dirLightShaderProgram != null) {
            this.dirLightShaderProgram.cleanUp();
        }
        if (this.pointLightShaderProgram != null) {
            this.pointLightShaderProgram.cleanUp();
        }
        if (this.gBuffer != null) {
            this.gBuffer.cleanUp();
        }
        if (this.bufferPassMesh != null) {
            this.bufferPassMesh.cleanUp();
        }
    }
    
}