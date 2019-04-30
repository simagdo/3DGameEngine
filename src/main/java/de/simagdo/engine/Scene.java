package de.simagdo.engine;

import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.items.GameItem;
import de.simagdo.engine.items.SkyBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private Map<Mesh, List<GameItem>> meshMap;
    private SkyBox skyBox;
    private SceneLight sceneLight;

    public Scene() {
        this.meshMap = new HashMap<>();
    }

    public Map<Mesh, List<GameItem>> getMeshMap() {
        return meshMap;
    }

    public void setMeshMap(GameItem[] gameItems) {
        for (int i = 0; i < gameItems.length; i++) {
            GameItem gameItem = gameItems[i];
            Mesh mesh = gameItem.getMesh();
            List<GameItem> list = this.meshMap.get(mesh);
            if (list == null) {
                list = new ArrayList<>();
                this.meshMap.put(mesh, list);
            }
            list.add(gameItem);
        }
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }
}