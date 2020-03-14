package de.simagdo.game.gui.gameMenu;

import de.simagdo.engine.gui.components.ClickableUI;
import de.simagdo.engine.gui.components.EventData;
import de.simagdo.engine.gui.components.MouseListener;
import de.simagdo.engine.gui.constraints.RatioConstraint;
import de.simagdo.engine.gui.constraints.RelativeConstraint;
import de.simagdo.engine.gui.uiComponent.UIComponent;
import de.simagdo.engine.gui.uiComponent.UIConstraints;
import de.simagdo.engine.gui.utils.Transitions;

public class MenuPageUI extends UIComponent {

    private static final float ANIMATION_TIME = 0.22f;
    private static final float Y_START = 0.2f;
    private static final float Y_GAP = 0.12f;
    private static final float BUTTON_X = 0.07f;
    private static final float BUTTON_RATIO = 6f;
    private static final float BUTTON_HEIGHT = 0.09f;
    private static final float BUTTON_DELAY = 0.06f;
    private final GameMenuUI gameMenu;

    public MenuPageUI(GameMenuUI gameMenu) {
        super.setLevel(1);
        this.gameMenu = gameMenu;
    }

    @Override
    protected void init() {
        int number = 0;
        this.addPlayButton(number++);
    }

    @Override
    protected void updateSelf() {

    }

    private void addPlayButton(int number) {
        this.addButton("Play", number, new MouseListener() {
            @Override
            public void eventOccured(EventData data) {
                gameMenu.display(false);
            }
        });
    }

    private ClickableUI addButton(String text, int buttonNumber, MouseListener listener) {
        ClickableUI button = new MenuButtonUI(text);
        UIConstraints constraints = new UIConstraints();
        constraints.setXConstraint(new RelativeConstraint(BUTTON_X));
        constraints.setYConstraint(new RelativeConstraint(Y_START + buttonNumber * Y_GAP));
        constraints.setWidthConstraint(new RatioConstraint(BUTTON_RATIO));
        constraints.setHeightConstraint(new RelativeConstraint(BUTTON_X));
        super.add(button, constraints);
        button.addMouseListener(listener);
        float delay = buttonNumber * BUTTON_DELAY;
        button.setDisplayAnimation(Transitions.slideXAndFade(-1.3f, ANIMATION_TIME), delay, delay);
        return button;
    }

}
