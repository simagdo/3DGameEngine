package de.simagdo.game.gui.main;

import de.simagdo.engine.GameEngine;

public class GameManager {

    private GameEngine engine;
    private GameEngineUI gameUI;

    public GameManager(GameEngine engine) {
        this.engine = engine;
    }

    public void init() {
        this.gameUI = new GameEngineUI(this.engine);
    }

}
