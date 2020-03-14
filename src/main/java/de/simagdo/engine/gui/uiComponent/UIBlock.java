package de.simagdo.engine.gui.uiComponent;

import de.simagdo.engine.graph.text.Texture;
import de.simagdo.engine.toolbox.colors.Color;

public class UIBlock extends UIComponent {

    private static final int NOT_ROUNDED = -1;
    private Texture texture;
    private Color overrideColor = null;
    private int[] clippingBounds = null;
    private boolean blurry = false;
    private boolean flipTexture = false;
    private float roundedCornerRadius = NOT_ROUNDED;

    public UIBlock(Texture texture) {
        this.texture = texture;
    }

    public UIBlock(Color color) {
        this.overrideColor = color.duplicate();
    }

    public void setRoundedCornerRadius(float roundedCornerRadius) {
        this.roundedCornerRadius = roundedCornerRadius;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Color getOverrideColor() {
        return this.overrideColor;
    }

    public boolean isBlurry() {
        return this.blurry;
    }

    public boolean isFlipTexture() {
        return this.flipTexture;
    }

    public int[] getClippingBounds() {
        return this.clippingBounds;
    }

    public float getRoundedCornerRadius() {
        return this.roundedCornerRadius;
    }

    public void setColor(Color color) {
        if (this.overrideColor == null) this.overrideColor = new Color();
        this.overrideColor.setColor(color);
    }

    protected void getRenderData(UIRenderBundle renderData) {
        renderData.addUIRenderData(this);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void updateSelf() {

    }

}
