package de.simagdo.engine.gui.uiRendering;

import de.simagdo.engine.graph.ShaderProgram;
import de.simagdo.engine.openglWrapper.shaders.*;
import de.simagdo.utils.Utils;

public class UIShader {

    private ShaderProgram uiShader;
    public UniformVec4 transform = new UniformVec4("transform");
    public UniformVec3 overrideColour = new UniformVec3("overrideColour");
    public UniformFloat alpha = new UniformFloat("alpha");
    public UniformSampler blurTexture = new UniformSampler("blurTexture");
    public UniformSampler guiTexture = new UniformSampler("guiTexture");
    public UniformBoolean flipTexture = new UniformBoolean("flipTexture");
    public UniformBoolean useBlur = new UniformBoolean("useBlur");
    public UniformBoolean useOverrideColour = new UniformBoolean("useOverrideColour");
    public UniformBoolean useTexture = new UniformBoolean("useTexture");
    public UniformFloat uiWidth = new UniformFloat("uiWidth");
    public UniformFloat uiHeight = new UniformFloat("uiHeight");
    public UniformFloat radius = new UniformFloat("radius");

    public UIShader() {
        try {
            this.uiShader = new ShaderProgram();

            this.uiShader.createVertexShader(Utils.loadResource("/glsl/ui/uiVertex.glsl"));
            this.uiShader.createFragmentShader(Utils.loadResource("/glsl/ui/uiFragment.glsl"));

            this.uiShader.createUniform("transform");
            this.uiShader.createUniform("overrideColour");
            this.uiShader.createUniform("alpha");
            this.uiShader.createUniform("blurTexture");
            this.uiShader.createUniform("guiTexture");
            this.uiShader.createUniform("flipTexture");
            this.uiShader.createUniform("useBlur");
            this.uiShader.createUniform("useOverrideColour");
            this.uiShader.createUniform("useTexture");
            this.uiShader.createUniform("uiWidth");
            this.uiShader.createUniform("uiHeight");
            this.uiShader.createUniform("radius");

            this.uiShader.bind();

            this.guiTexture.loadTexUnit(0);
            this.blurTexture.loadTexUnit(1);

            this.uiShader.unbind();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ShaderProgram getUiShader() {
        return uiShader;
    }
}
