package de.simagdo.game.gui.gameMenu;

import de.simagdo.engine.graph.text.Texture;
import de.simagdo.engine.gui.uiComponent.UIBlock;
import de.simagdo.engine.gui.utils.Transitions;

public class MenuBackgroundUI extends UIBlock {

    private static final float FADE_TIME = 0.5f;

    public MenuBackgroundUI(Texture texture) {
        super(texture);
    }

    @Override
    protected void init() {
        super.init();
        super.setDisplayAnimation(Transitions.fade(FADE_TIME));
    }

}
