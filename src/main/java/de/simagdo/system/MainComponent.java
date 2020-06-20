package de.simagdo.system;

import de.simagdo.engine.world.chunk.Chunk;
import de.simagdo.game.gui.main.GameManager;
import de.simagdo.game.managing.GameConfigs;
import de.simagdo.game.managing.GameSettings;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

public class MainComponent {

    public static void main(String[] args) {
        try {
            GameConfigs configs = GameSettings.getConfig();
            GameManager.init(configs);

            //GameManager.run();

            //glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

            while (!GameManager.readyToClose()) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                glfwSwapBuffers(GameManager.getEngine().getWindow().getWindowId());

                GameManager.update();

            }

            GameManager.cleanUp();

            /*Chunk chunk = new Chunk(0,0);
            chunk.createChunk();*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
