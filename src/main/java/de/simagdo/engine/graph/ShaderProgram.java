package de.simagdo.engine.graph;

import org.joml.Matrix4f;
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
    private final Map<String, Integer> uniforms;

    public ShaderProgram() throws Exception {
        this.programId = glCreateProgram();
        if (this.programId == 0) throw new Exception("Could not create Shader");
        this.uniforms = new HashMap<>();
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) throw new Exception("Error createing shader. Type: " + shaderType);

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0)
            throw new Exception("Error compiling Shader code: " + shaderCode);

        glAttachShader(this.programId, shaderId);

        return shaderId;
    }

    public void link() throws Exception {
        glLinkProgram(this.programId);

        if (glGetProgrami(this.programId, GL_LINK_STATUS) == 0)
            throw new Exception("Error compiling Shader code: " + glGetProgramInfoLog(this.programId, 1024));

        if (this.vertexShaderId != 0) glDetachShader(this.programId, this.vertexShaderId);
        if (this.fragmentShaderId != 0) glDetachShader(this.programId, this.fragmentShaderId);

        glValidateProgram(this.programId);
        if (glGetProgrami(this.programId, GL_VALIDATE_STATUS) == 0)
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(this.programId, 1024));

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
        this.createUniform(uniformName + ".ambient");
        this.createUniform(uniformName + ".diffuse");
        this.createUniform(uniformName + ".specular");
        this.createUniform(uniformName + ".hasTexture");
        this.createUniform(uniformName + ".reflectance");
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
        this.setUniform(uniformName + ".ambient", material.getAmbientColour());
        this.setUniform(uniformName + ".diffuse", material.getDiffuseColour());
        this.setUniform(uniformName + ".specular", material.getSpecularColour());
        this.setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        this.setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(this.programId, uniformName);
        if (uniformLocation < 0) throw new Exception("Could not find uniform: " + uniformName);
        this.uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        //Dump the matrix into a float Buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            value.get(buffer);
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
        glUniform3f(this.uniforms.get(uniformName), value.y, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(this.uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void bind() {
        glUseProgram(this.programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanUp() {
        unbind();
        if (this.programId != 0) glDeleteProgram(this.programId);
    }

}