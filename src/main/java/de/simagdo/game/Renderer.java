package de.simagdo.game;

import de.simagdo.engine.GameItem;
import de.simagdo.engine.Window;
import de.simagdo.engine.graph.*;
import de.simagdo.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    private ShaderProgram shaderProgram;
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private Matrix4f projectionMatrix;
    private Transformation transformation;
    private float specularPower;

    public Renderer() {
        this.transformation = new Transformation();
        this.specularPower = 10f;
    }

    public void init(Window window) throws Exception {
        // Create shader
        this.shaderProgram = new ShaderProgram();
        this.shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        this.shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        this.shaderProgram.link();

        // Create uniforms for modelView and projection matrices and texture
        this.shaderProgram.createUniform("projectionMatrix");
        this.shaderProgram.createUniform("modelViewMatrix");
        this.shaderProgram.createUniform("texture_sampler");

        //Create uniform for material
        this.shaderProgram.createMaterialUniform("material");

        // Create lighting related uniforms
        this.shaderProgram.createUniform("specularPower");
        this.shaderProgram.createUniform("ambientLight");
        this.shaderProgram.createPointLightUniform("pointLight");
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameItem[] gameItems, Vector3f ambientLight, PointLight pointLight) {
        this.clear();

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        //Update Projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        this.shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        //Update Light Uniforms
        this.shaderProgram.setUniform("ambientLight", ambientLight);
        this.shaderProgram.setUniform("specularPower", specularPower);

        // Get a copy of the light object and transform its position to view coordinates
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.mul(viewMatrix);
        lightPos.x = aux.x;
        lightPos.y = aux.y;
        lightPos.z = aux.z;
        shaderProgram.setUniform("pointLight", currPointLight);

        shaderProgram.setUniform("texture_sampler", 0);

        // Render each gameItem
        for (GameItem gameItem : gameItems) {
            Mesh mesh = gameItem.getMesh();
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mesh for this game item
            shaderProgram.setUniform("material", mesh.getMaterial());
            mesh.render();
        }

        this.shaderProgram.unbind();
    }

    public void cleanUp() {
        if (shaderProgram != null) shaderProgram.cleanUp();
    }

}