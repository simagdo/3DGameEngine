package de.simagdo.game.gui.gameMenu;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.gui.constraints.ConstraintFactory;
import de.simagdo.engine.gui.uiComponent.UIComponent;
import de.simagdo.game.managing.GameState;

import java.io.File;
import java.util.Stack;

import static de.simagdo.engine.gui.mainUI.UIMaster.getContainer;

public class GameMenuUI extends UIComponent {

    private static final File BACKGROUND = new File("/GUIs/background_temp.png");
    private static final float IMAGE_ASPECT_RATIO = 16f / 9;
    private static final float CONTENT_Y = 0.3f;
    private final GameEngine engine;
    private MenuBackgroundUI backgroundUI;
    private Stack<UIComponent> currentPages = new Stack<>();
    private UIComponent pageBeingAdded = null;
    private boolean transitioning = false;

    public GameMenuUI(GameEngine engine) {
        super.setLevel(5);
        this.engine = engine;
        getContainer().add(this, ConstraintFactory.getFill());
    }

    public void showPage(UIComponent page) {
        this.transitioning = true;
        this.pageBeingAdded = page;
        this.currentPages.peek().display(false);
    }

    public void goBack() {
        this.showPage(null);
    }

    protected void closeGame() {
        this.engine.close();
    }

    private void addPage(UIComponent page) {
        super.addInStyle(page, ConstraintFactory.getRelative(0, CONTENT_Y, 1f, 1f - CONTENT_Y));
        this.currentPages.push(page);
    }

    @Override
    public void display(boolean display) {
        super.display(display);
        if (display) this.engine.getStateManager().suggestState(GameState.MAIN_MENU, true);
        else this.engine.getStateManager().endState(GameState.MAIN_MENU);
    }

    @Override
    protected void init() {
        this.addPage(new MenuPageUI(this));
        this.engine.getStateManager().endState(GameState.SPLASH_SCREEN);
        this.engine.getStateManager().suggestState(GameState.MAIN_MENU, true);
    }

    @Override
    protected void updateSelf() {

    }
}
