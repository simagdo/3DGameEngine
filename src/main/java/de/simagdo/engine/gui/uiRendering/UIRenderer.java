package de.simagdo.engine.gui.uiRendering;

import de.simagdo.engine.gui.uiComponent.UIBlock;
import de.simagdo.engine.openglWrapper.openglObjects.Attribute;
import de.simagdo.engine.openglWrapper.openglObjects.VAO;
import de.simagdo.engine.openglWrapper.openglUtils.OpenglUtils;
import de.simagdo.engine.openglWrapper.openglUtils.VAOUtils;
import de.simagdo.engine.toolbox.colors.Color;
import org.lwjgl.opengl.GL11;

public class UIRenderer {

    private static final float[] POSITIONS = {0, 0, 0, 1, 1, 0, 1, 1};
    private final UIShader shader;
    private final VAO vao;

    public UIRenderer() {
        this.shader = new UIShader();
        this.vao = VAOUtils.createVao(POSITIONS, new Attribute(0, GL11.GL_FLOAT, 2));
    }

    public void render(Iterable<UIBlock> uiData, int displayWidth, int displayHeight, float uiSize, int blurredSceneTexture) {
        prepare(blurredSceneTexture);
        for (UIBlock ui : uiData) {
            renderUi(ui, displayWidth, displayHeight, uiSize);
        }
        endRendering();
    }

    public void cleanUp() {
        this.shader.getUiShader().cleanUp();
    }

    private void prepare(int blurryImage) {
        this.initOpenglSettings();
        this.shader.getUiShader().bind();
        OpenglUtils.bindTextureToBank(blurryImage, 1);
        this.vao.bind();
    }

    private void renderUi(UIBlock ui, int displayWidth, int displayHeight, float uiSize) {
        if (ui.getTexture() != null) {
            OpenglUtils.bindTextureToBank(ui.getTexture().getId(), 0);
        }
        this.setScissorTest(ui.getClippingBounds());
        this.setUniformValues(ui, displayWidth, displayHeight, uiSize);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    }

    private void setUniformValues(UIBlock ui, int displayWidth, int displayHeight, float uiSize) {
        this.shader.transform.loadVec4(ui.getAbsX(), ui.getAbsY(), ui.getAbsWidth(), ui.getAbsHeight());
        this.setTextureUniforms(ui);
        this.setColourUniforms(ui.getOverrideColor());
        this.setRoundedCornerUniforms(ui, displayWidth, displayHeight, uiSize);
    }

    private void endRendering() {
        this.disableOpenglSettings();
        this.vao.unbind();
        this.shader.getUiShader().unbind();
    }

    private void initOpenglSettings() {
        OpenglUtils.setStandardSettings(true, false, false);
        OpenglUtils.enableAlphaBlending();
    }

    private void disableOpenglSettings() {
        OpenglUtils.disableScissorTest();
        OpenglUtils.disableBlending();
    }

    private void setTextureUniforms(UIBlock ui) {
        this.shader.alpha.loadFloat(ui.getTotalAlpha());
        this.shader.useBlur.loadBoolean(ui.isBlurry());
        this.shader.flipTexture.loadBoolean(ui.isFlipTexture());
        this.shader.useTexture.loadBoolean(ui.getTexture() != null);
    }

    private void setColourUniforms(Color color) {
        this.shader.useOverrideColour.loadBoolean(color != null);
        if (color != null) {
            this.shader.overrideColour.loadVec3(color.getR(), color.getG(), color.getB());
        }
    }

    private void setRoundedCornerUniforms(UIBlock ui, int displayWidth, int displayHeight, float uiSize) {
        float roundedRadius = ui.getRoundedCornerRadius();
        if (roundedRadius <= 0 || uiSize < 1) {
            this.shader.radius.loadFloat(-1);
            return;
        }
        float pixelWidth = ui.getAbsWidth() * displayWidth;
        this.shader.uiWidth.loadFloat(pixelWidth);
        float pixelHeight = ui.getAbsHeight() * displayHeight;
        this.shader.uiHeight.loadFloat(pixelHeight);
        float maxRadius = Math.min(pixelWidth, pixelHeight) / 2f;
        roundedRadius = Math.min(maxRadius, roundedRadius * uiSize);
        this.shader.radius.loadFloat(roundedRadius);
    }

    private void setScissorTest(int[] bounds) {
        if (bounds == null) {
            OpenglUtils.disableScissorTest();
        } else {
            OpenglUtils.enableScissorTest(bounds[0], bounds[1], bounds[2], bounds[3]);
        }
    }


}
