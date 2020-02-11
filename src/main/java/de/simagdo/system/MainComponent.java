package de.simagdo.system;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.IGameLogic;
import de.simagdo.engine.window.WindowOptions;
import de.simagdo.game.DummyGame;

public class MainComponent {

    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new DummyGame();
            WindowOptions windowOptions = new WindowOptions();
            windowOptions.cullFace = true;
            windowOptions.showFPS = true;
            windowOptions.compatibleProfile = true;
            windowOptions.antialiasing = true;
            windowOptions.frustumCulling = true;
            GameEngine engine = new GameEngine("Game", 800, 600, vSync, windowOptions, gameLogic);
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
