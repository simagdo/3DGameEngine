package de.simagdo.game;

import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.IHud;
import de.simagdo.engine.graph.text.FontTexture;
import de.simagdo.engine.graph.text.TextItem;
import de.simagdo.engine.Window;
import de.simagdo.engine.graph.Material;
import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.graph.OBJLoader;
import org.joml.Vector4f;

import java.awt.*;

public class Hud implements IHud {

    private final GameItem[] gameItems;
    private final TextItem statusTextItem;
    private final GameItem compassItem;
    private static final Font FONT = new Font("Arial", Font.PLAIN, 20);
    private static final String CHARSET = "ISO-8859-1";

    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));

        Mesh mesh = OBJLoader.loadMesh("/models/compass.obj");
        Material material = new Material();
        material.setAmbientColour(new Vector4f(1, 0, 0, 1));
        mesh.setMaterial(material);
        this.compassItem = new GameItem(mesh);
        this.compassItem.setScale(40.0f);
        this.compassItem.setPosition(0, 0, 180f);

        this.gameItems = new GameItem[]{this.statusTextItem, this.compassItem};
    }

    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }

    public void rotateCompass(float angle) {
        this.compassItem.setRotation(0, 0, 180 + angle);
    }

    @Override
    public GameItem[] getGameItems() {
        return this.gameItems;
    }

    public void updateSize(Window window) {
        this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
        this.compassItem.setPosition(window.getWidth() - 40f, 50f, 0);
    }

}