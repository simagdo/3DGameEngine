package de.simagdo.engine.graph;

import de.simagdo.engine.items.GameItem;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CameraBoxSelectionDetector {

    private final Vector3f max;
    private final Vector3f min;
    private final Vector2f nearFar;
    private Vector3f dir;

    public CameraBoxSelectionDetector() {
        this.dir = new Vector3f();
        this.min = new Vector3f();
        this.max = new Vector3f();
        this.nearFar = new Vector2f();
    }

    public void selectGameItem(GameItem[] gameItems, Camera camera) {
        GameItem selectedGameItem = null;
        float closestDistance = Float.POSITIVE_INFINITY;

        dir = camera.getViewMatrix().positiveZ(dir).negate();
        for (GameItem gameItem : gameItems) {
            gameItem.setSelected(false);
            min.set(gameItem.getPosition());
            max.set(gameItem.getPosition());
            min.add(-gameItem.getScale(), -gameItem.getScale(), -gameItem.getScale());
            max.add(gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
            if (Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, nearFar) && nearFar.x < closestDistance) {
                closestDistance = nearFar.x;
                selectedGameItem = gameItem;
            }
        }

        if (selectedGameItem != null) {
            selectedGameItem.setSelected(true);
        }
    }

}
