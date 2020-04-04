package de.simagdo.system;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.IGameLogic;
import de.simagdo.engine.window.WindowOptions;
import de.simagdo.game.DummyGame;
import de.simagdo.game.gui.main.GameManager;

public class MainComponent {

    public static void main(String[] args) {
        try {
            GameManager manager = new GameManager();
            manager.init();
            manager.getEngine().start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
