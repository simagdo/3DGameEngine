package de.simagdo.engine.graph;

import de.simagdo.engine.graph.lights.DirectionalLight;
import de.simagdo.engine.graph.lights.PointLight;
import de.simagdo.engine.graph.lights.SpotLight;
import de.simagdo.engine.graph.weather.Fog;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private int geometryShaderId;
    private final Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception {
        this.programId = glCreateProgram();
        if (this.programId == 0) {
            throw new Exception("Could not create Shader");
        }
        this.uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(this.programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform:" + uniformName);
        }
        this.uniforms.put(uniformName, uniformLocation);
    }

    public void createUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            this.createUniform(uniformName + "[" + i + "]");
        }
    }

    public void createPointLightUniform(String uniformName) throws Exception {
        this.createUniform(uniformName + ".colour");
        this.createUniform(uniformName + ".position");
        this.createUniform(uniformName + ".intensity");
        this.createUniform(uniformName + ".att.constant");
        this.createUniform(uniformName + ".att.linear");
        this.createUniform(uniformName + ".att.exponent");
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        this.createUniform(uniformName + ".diffuse");
        this.createUniform(uniformName + ".specular");
        this.createUniform(uniformName + ".hasTexture");
        this.createUniform(uniformName + ".hasNormalMap");
        this.createUniform(uniformName + ".reflectance");
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Dump the matrix into a float buffer
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
            glUniformMatrix4fv(this.uniforms.get(uniformName), false, buffer);
        }
    }

    public void setUniform(String uniformName, Matrix4f[] matrices) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = matrices.length;
            // Dump the matrix into a float buffer
            FloatBuffer buffer = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                matrices[i].get(16 * i, buffer);
            }
            glUniformMatrix4fv(this.uniforms.get(uniformName), false, buffer);
        }
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(this.uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(this.uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(this.uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(this.uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, PointLight pointLight) {
        this.setUniform(uniformName + ".colour", pointLight.getColor());
        this.setUniform(uniformName + ".position", pointLight.getPosition());
        this.setUniform(uniformName + ".intensity", pointLight.getIntensity());
        Attenuation att = pointLight.getAttenuation();
        this.setUniform(uniformName + ".att.constant", att.getConstant());
        this.setUniform(uniformName + ".att.linear", att.getLinear());
        this.setUniform(uniformName + ".att.exponent", att.getExponent());
    }

    public void setUniform(String uniformName, Material material) {
        this.setUniform(uniformName + ".diffuse", material.getDiffuseColour());
        this.setUniform(uniformName + ".specular", material.getSpecularColour());
        this.setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        this.setUniform(uniformName + ".hasNormalMap", material.hasNormalMap() ? 1 : 0);
        this.setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        this.createUniform(uniformName + ".colour");
        this.createUniform(uniformName + ".direction");
        this.createUniform(uniformName + ".intensity");
    }

    public void setUniform(String uniformName, DirectionalLight directionalLight) {
        this.setUniform(uniformName + ".colour", directionalLight.getColor());
        this.setUniform(uniformName + ".direction", directionalLight.getDirection());
        this.setUniform(uniformName + ".intensity", directionalLight.getIntensity());
    }

    public void createSpotLightUniform(String uniformName) throws Exception {
        this.createPointLightUniform(uniformName + ".pl");
        this.createUniform(uniformName + ".conedir");
        this.createUniform(uniformName + ".cutoff");
    }

    public void setUniform(String uniformName, SpotLight spotLight) {
        this.setUniform(uniformName + ".pl", spotLight.getPointLight());
        this.setUniform(uniformName + ".conedir", spotLight.getConeDirection());
        this.setUniform(uniformName + ".cutoff", spotLight.getCutOff());
    }

    public void createFogUniform(String uniformName) throws Exception {
        this.createUniform(uniformName + ".activeFog");
        this.createUniform(uniformName + ".colour");
        this.createUniform(uniformName + ".density");
    }

    public void setUniform(String uniformName, Fog fog) {
        this.setUniform(uniformName + ".activeFog", fog.isActive() ? 1 : 0);
        this.setUniform(uniformName + ".colour", fog.getColour());
        this.setUniform(uniformName + ".density", fog.getDensity());
    }

    public void setUniform(String uniformName, Matrix4f value, int index) {
        this.setUniform(uniformName + "[" + index + "]", value);
    }

    public void setUniform(String uniformName, float value, int index) {
        this.setUniform(uniformName + "[" + index + "]", value);
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    public void createPointLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            this.createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            this.createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void setUniform(String uniformName, PointLight[] pointLights) {
        int numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            this.setUniform(uniformName, pointLights[i], i);
        }
    }

    public void setUniform(String uniformName, PointLight pointLight, int pos) {
        this.setUniform(uniformName + "[" + pos + "]", pointLight);
    }

    public void setUniform(String uniformName, SpotLight[] spotLights) {
        int numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            this.setUniform(uniformName, spotLights[i], i);
        }
    }

    public void setUniform(String uniformName, SpotLight spotLight, int pos) {
        this.setUniform(uniformName + "[" + pos + "]", spotLight);
    }

    public void setUniform(String uniformName, float x, float y) {
        glUniform2f(this.uniforms.get(uniformName), x, y);
    }

    public void setUniform(String uniformName, Vector2f value) {
        glUniform2f(this.uniforms.get(uniformName), value.x, value.y);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(this.programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(this.programId);
        if (glGetProgrami(this.programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(this.programId, 1024));
        }

        if (this.vertexShaderId != 0) {
            glDetachShader(this.programId, this.vertexShaderId);
        }
        if (this.geometryShaderId != 0) {
            glDetachShader(this.programId, this.geometryShaderId);
        }
        if (this.fragmentShaderId != 0) {
            glDetachShader(this.programId, this.fragmentShaderId);
        }

        glValidateProgram(this.programId);
        if (glGetProgrami(this.programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(this.programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(this.programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanUp() {
        this.unbind();
        if (this.programId != 0) {
            glDeleteProgram(this.programId);
        }
    }
}