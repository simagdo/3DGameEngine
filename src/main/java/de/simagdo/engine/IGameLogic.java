package de.simagdo.engine;

import de.simagdo.engine.window.Window;

public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput, Window window);

    void render(Window window);

    void cleanUp();

}
