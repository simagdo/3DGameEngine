package de.simagdo.system;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.IGameLogic;
import de.simagdo.game.DummyGame;

public class MainComponent {

    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new DummyGame();
            GameEngine engine = new GameEngine("Game", 600, 480, vSync, gameLogic);
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
