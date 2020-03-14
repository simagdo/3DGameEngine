package de.simagdo.game.gui.gameMenu;

import de.simagdo.engine.gui.components.ClickableUI;
import de.simagdo.engine.gui.constraints.ConstraintFactory;
import de.simagdo.engine.gui.transitions.Transition;
import de.simagdo.engine.gui.uiComponent.UIBlock;
import de.simagdo.engine.gui.utils.Transitions;
import de.simagdo.engine.toolbox.colors.Color;
import de.simagdo.engine.toolbox.colors.UIColors;

public class MenuButtonUI extends ClickableUI {

    private static final float TEXT_X = 0.05f;
    private static final float TEXT_Y = 0.25f;
    private static final float TEXT_SLIDE_X = 1.5f;
    private static final Transition MOUSE_OVER_ANIMATION = Transitions.scaleUpFromLeft(1.07f, 0.15f);
    private static final Color NORMAL_COLOR = UIColors.DARK_GREY;
    private static final Color HOVER_COLOR = UIColors.ACCENT;
    private static final int ROUNDED_RADIUS = 4;
    private static final float ALPHA = 0.75f;
    private final String text;
    private UIBlock block;

    public MenuButtonUI(String text) {
        this.text = text;
    }

    @Override
    protected void init() {
        this.addBlock();
        //TODO add this Method
        //this.addText
    }

    @Override
    protected void updateSelf() {
        super.updateSelf();
    }

    @Override
    protected void mouseOver(boolean newMouseOverState) {
        super.mouseOver(newMouseOverState);
        if (newMouseOverState) {
            this.block.setColor(HOVER_COLOR);
            this.getAnimator().applyModifier(MOUSE_OVER_ANIMATION, false, 0);
        } else {
            this.block.setColor(NORMAL_COLOR);
            this.getAnimator().applyModifier(MOUSE_OVER_ANIMATION, true, 0);
        }
    }

    private void addBlock() {
        this.block = new UIBlock(UIColors.DARK_GREY);
        this.block.setAlpha(ALPHA);
        this.block.setRoundedCornerRadius(ROUNDED_RADIUS);
        super.add(this.block, ConstraintFactory.getFill());
    }

}
