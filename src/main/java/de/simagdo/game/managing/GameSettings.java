package de.simagdo.game.managing;

public class GameSettings {

    public static GameConfigs getConfig() {
        GameConfigs configs = GameConfigs.getDefaultConfig();
        configs.windowTitle = "The lonely Wizard";
        configs.vSync = true;
        configs.fps = 60;
        configs.windowWidth = 1920;
        configs.windowHeight = 1080;
        return configs;
    }

}
