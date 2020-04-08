package de.simagdo.system;

import de.simagdo.engine.GameEngine;
import de.simagdo.engine.IGameLogic;
import de.simagdo.engine.window.WindowOptions;
import de.simagdo.game.DummyGame;
import de.simagdo.game.gui.main.GameManager;
import de.simagdo.game.managing.GameConfigs;
import de.simagdo.game.managing.GameSettings;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.*;

public class MainComponent {

    public static void main(String[] args) {
        try {
            GameConfigs configs = GameSettings.getConfig();
            GameManager.init(configs);

            glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

            while (!glfwWindowShouldClose(GameManager.getEngine().getWindow().getWindowId())) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                glfwSwapBuffers(GameManager.getEngine().getWindow().getWindowId());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
