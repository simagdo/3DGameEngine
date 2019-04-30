package de.simagdo.engine;

import de.simagdo.engine.items.GameItem;

public interface IHud {

    GameItem[] getGameItems();

    default void cleanup(){
        for (GameItem gameItem : getGameItems()){
            gameItem.getMesh().cleanUp();
        }
    }

}