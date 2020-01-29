package de.simagdo.engine;

import de.simagdo.engine.items.GameItem;

public interface IHud {

    GameItem[] getGameItems();

    default void cleanup(){
        for (GameItem gameItem : this.getGameItems()){
            gameItem.getMesh().cleanUp();
        }
    }

}