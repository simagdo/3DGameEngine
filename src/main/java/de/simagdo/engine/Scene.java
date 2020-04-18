package de.simagdo.engine;

import de.simagdo.engine.graph.InstancedMesh;
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
    private final Map<InstancedMesh, List<GameItem>> instancedMeshMap;
    private SkyBox skyBox;
    private SceneLight sceneLight;
    private Fog fog;
    private IParticleEmitter[] iParticleEmitters;
    private boolean renderShadows;

    public Scene() {
        this.meshMap = new HashMap<>();
        this.instancedMeshMap = new HashMap<>();
        this.fog = Fog.NOFOG;
        this.renderShadows = true;
    }

    public Map<Mesh, List<GameItem>> getMeshMap() {
        return meshMap;
    }

    public void setGameItems(GameItem[] gameItems) {
        // Create a map of meshes to speed up rendering
        for (GameItem gameItem : gameItems) {
            if (gameItem != null) {
                Mesh[] meshes = gameItem.getMeshes();
                for (Mesh mesh : meshes) {
                    boolean instancedMesh = mesh instanceof InstancedMesh;
                    List<GameItem> list = instancedMesh ? this.instancedMeshMap.get(mesh) : this.meshMap.get(mesh);
                    if (list == null) {
                        list = new ArrayList<>();
                        if (instancedMesh) this.instancedMeshMap.put((InstancedMesh) mesh, list);
                        else this.meshMap.put(mesh, list);
                    }
                    list.add(gameItem);
                }
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

    public Map<InstancedMesh, List<GameItem>> getInstancedMeshMap() {
        return instancedMeshMap;
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

    public boolean isRenderShadows() {
        return renderShadows;
    }

    public void setRenderShadows(boolean renderShadows) {
        this.renderShadows = renderShadows;
    }

    public void cleanUp() {
        for (Mesh mesh : this.instancedMeshMap.keySet()) mesh.cleanUp();
        if (this.iParticleEmitters != null)
            for (IParticleEmitter particleEmitter : this.getParticleEmitters()) particleEmitter.cleanUp();
    }

}