package de.simagdo.engine.world.tile;

import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.GameItemType;

public class Tile {

    //TODO Maybe add an UUID to identify the Tile?

    private String objModel;
    private String texture;
    private final GameItem gameItem;
    private GameItemType gameItemType;

    public Tile() {
        this(null, null, null, null);
    }

    public Tile(String objModel, String texture, GameItem gameItem, GameItemType gameItemType) {
        this.objModel = objModel;
        this.texture = texture;
        this.gameItem = gameItem;
        this.gameItemType = gameItemType;
    }

    public String getObjModel() {
        return objModel;
    }

    public String getTexture() {
        return texture;
    }

    public GameItem getGameItem() {
        return gameItem;
    }

    public GameItemType getGameItemType() {
        return gameItemType;
    }

    @Override
    public String toString() {
        return "Tile{" +
                ", objModel='" + objModel + '\'' +
                ", texture='" + texture + '\'' +
                ", gameItem=" + gameItem +
                ", gameItemType=" + gameItemType +
                '}';
    }
}
