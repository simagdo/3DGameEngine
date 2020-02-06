package de.simagdo.engine;

import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.graph.particles.IParticleEmitter;
import de.simagdo.engine.graph.weather.Fog;
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
    private Fog fog;
    private IParticleEmitter[] iParticleEmitters;

    public Scene() {
        this.meshMap = new HashMap<>();
        this.fog = Fog.NOFOG;
    }

    public Map<Mesh, List<GameItem>> getMeshMap() {
        return meshMap;
    }

    public void setGameItems(GameItem[] gameItems) {
        // Create a map of meshes to speed up rendering
        for (GameItem gameItem : gameItems) {
            Mesh[] meshes = gameItem.getMeshes();
            for (Mesh mesh : meshes) {
                List<GameItem> list = meshMap.computeIfAbsent(mesh, key -> new ArrayList<>());
                list.add(gameItem);
            }
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

    public void setMeshMap(Map<Mesh, List<GameItem>> meshMap) {
        this.meshMap = meshMap;
    }

    public Fog getFog() {
        return fog;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public IParticleEmitter[] getParticleEmitters() {
        return iParticleEmitters;
    }

    public void setParticleEmitters(IParticleEmitter[] iParticleEmitters) {
        this.iParticleEmitters = iParticleEmitters;
    }

    public void cleanUp() {
        for (Mesh mesh : this.meshMap.keySet()) mesh.cleanUp();
        for (IParticleEmitter particleEmitter : this.getParticleEmitters()) particleEmitter.cleanUp();
    }

}