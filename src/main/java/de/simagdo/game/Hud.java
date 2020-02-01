package de.simagdo.game;

import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.IHud;
import de.simagdo.engine.graph.text.FontTexture;
import de.simagdo.engine.graph.text.TextItem;
import de.simagdo.engine.window.Window;
import org.joml.Vector4f;

import java.awt.*;

public class Hud implements IHud {

    private final GameItem[] gameItems;
    private final TextItem statusTextItem;
    private static final Font FONT = new Font("Arial", Font.PLAIN, 20);
    private static final String CHARSET = "ISO-8859-1";

    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));

        this.gameItems = new GameItem[]{this.statusTextItem};
    }

    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }

    @Override
    public GameItem[] getGameItems() {
        return this.gameItems;
    }

    public void updateSize(Window window) {
        this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
    }

}