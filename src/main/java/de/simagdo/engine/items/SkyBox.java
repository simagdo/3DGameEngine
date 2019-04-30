package de.simagdo.engine.items;

import de.simagdo.engine.graph.Material;
import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.graph.OBJLoader;
import de.simagdo.engine.graph.text.Texture;

public class SkyBox extends GameItem {

    public SkyBox(String objModel, String textureFile) throws Exception {
        super();
        Mesh skyBoxMesh = OBJLoader.loadMesh(objModel);
        Texture skyBoxTexture = new Texture(textureFile);
        skyBoxMesh.setMaterial(new Material(skyBoxTexture, 0.0f));
        this.setMesh(skyBoxMesh);
        this.setPosition(0, 0, 0);
    }

}