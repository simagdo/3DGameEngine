package de.simagdo.engine.gui.components;

import de.simagdo.engine.gui.constraints.ConstraintFactory;
import de.simagdo.engine.gui.uiComponent.UIBlock;
import de.simagdo.engine.toolbox.colors.Color;
import de.simagdo.engine.toolbox.colors.UIColors;

public class ButtonUI extends ClickableUI {

    private Color hoverColor = UIColors.ACCENT;
    private Color normalColor;
    private final UIBlock block;

    public ButtonUI(UIBlock block) {
        this.block = block;
        this.normalColor = block.getOverrideColor().duplicate();
    }

    private void addBlock() {
        this.block.setColor(this.normalColor);
        super.add(this.block, ConstraintFactory.getFill());
    }

    @Override
    protected void init() {
        this.addBlock();
    }

    @Override
    protected void mouseOver(boolean newMouseOverState) {
        super.mouseOver(newMouseOverState);
        if (newMouseOverState) this.block.setColor(this.hoverColor);
        else this.block.setColor(this.normalColor);
    }

}
