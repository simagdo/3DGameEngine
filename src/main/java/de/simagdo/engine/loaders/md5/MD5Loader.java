package de.simagdo.engine.loaders.md5;

import de.simagdo.engine.graph.Material;
import de.simagdo.engine.graph.Mesh;
import de.simagdo.engine.graph.text.Texture;
import de.simagdo.engine.items.GameItem;
import de.simagdo.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class MD5Loader {

    private static final String NORMAL_FILE_SUFFIX = "_normal";

    public static GameItem process(MD5Model md5Model, Vector4f defaultColour) throws Exception {
        List<MD5Mesh> md5MeshList = md5Model.getMeshes();

        int index = 0;

        List<Mesh> list = new ArrayList<>();
        System.out.println("Size MD5Model: " + md5Model.getMeshes().size());
        for (MD5Mesh md5Mesh : md5Model.getMeshes()) {
            System.out.println("Index: " + index);
            Mesh mesh = generateMesh(md5Model, md5Mesh, defaultColour);
            index++;
            handleTexture(mesh, md5Mesh, defaultColour);
            list.add(mesh);
        }
        Mesh[] meshes = new Mesh[list.size()];
        meshes = list.toArray(meshes);

        return new GameItem(meshes);
    }

    private static Mesh generateMesh(MD5Model md5Model, MD5Mesh md5Mesh, Vector4f defaultColour) throws Exception {
        List<VertexInfo> vertexInfoList = new ArrayList<>();
        List<Float> textCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        List<MD5Vertex> vertices = md5Mesh.getVertices();
        List<MD5Weight> weights = md5Mesh.getWeights();
        List<MD5JointData> joints = md5Model.getJointInfo().getJoints();

        System.out.println("Vertices: "+ vertices.size() + ", Weights: " + weights.size() + ", Joints: " + joints.size());

        for (MD5Vertex vertex : vertices) {
            Vector3f vertexPos = new Vector3f();
            Vector2f vertexTextCoords = vertex.getTextCoords();
            textCoords.add(vertexTextCoords.x);
            textCoords.add(vertexTextCoords.y);

            int startWeight = vertex.getStartWeight();
            int numWeights = vertex.getWeightCount();

            System.out.println("StartWeight: " + startWeight + ", NumWeight: " + numWeights);

            for (int i = startWeight; i < startWeight + numWeights; i++) {
                MD5Weight weight = weights.get(i);
                MD5JointData joint = joints.get(weight.getJointIndex());
                Vector3f rotatedPos = new Vector3f(weight.getPosition()).rotate(joint.getOrientation());
                Vector3f acumPos = new Vector3f(joint.getPosition()).add(rotatedPos);
                acumPos.mul(weight.getBias());
                vertexPos.add(acumPos);
            }

            vertexInfoList.add(new VertexInfo(vertexPos));
        }

        for (MD5Triangle tri : md5Mesh.getTriangles()) {
            indices.add(tri.getVertex0());
            indices.add(tri.getVertex1());
            indices.add(tri.getVertex2());

            // Normals
            VertexInfo v0 = vertexInfoList.get(tri.getVertex0());
            VertexInfo v1 = vertexInfoList.get(tri.getVertex1());
            VertexInfo v2 = vertexInfoList.get(tri.getVertex2());
            Vector3f pos0 = v0.position;
            Vector3f pos1 = v1.position;
            Vector3f pos2 = v2.position;

            Vector3f normal = (new Vector3f(pos2).sub(pos0)).cross(new Vector3f(pos1).sub(pos0));

            v0.normal.add(normal);
            v1.normal.add(normal);
            v2.normal.add(normal);
        }

        // Once the contributions have been added, normalize the result
        for (VertexInfo v : vertexInfoList) {
            v.normal.normalize();
        }

        float[] positionsArr = VertexInfo.toPositionsArr(vertexInfoList);
        float[] textCoordsArr = Utils.listToArray(textCoords);
        float[] normalsArr = VertexInfo.toNormalArr(vertexInfoList);
        int[] indicesArr = indices.stream().mapToInt(i -> i).toArray();
        Mesh mesh = new Mesh(positionsArr, textCoordsArr, normalsArr, indicesArr);

        return mesh;
    }

    private static void handleTexture(Mesh mesh, MD5Mesh md5Mesh, Vector4f defaultColour) throws Exception {
        String texturePath = md5Mesh.getTexture();
        if (texturePath != null && texturePath.length() > 0) {
            Texture texture = new Texture(texturePath);
            Material material = new Material(texture);

            // Handle normal Maps;
            int pos = texturePath.lastIndexOf(".");
            if (pos > 0) {
                String basePath = texturePath.substring(0, pos);
                String extension = texturePath.substring(pos, texturePath.length());
                String normalMapFileName = basePath + NORMAL_FILE_SUFFIX + extension;
                if (Utils.existsResourceFile(normalMapFileName)) {
                    Texture normalMap = new Texture(normalMapFileName);
                    material.setNormalMap(normalMap);
                }
            }
            mesh.setMaterial(material);
        } else {
            mesh.setMaterial(new Material(defaultColour, 1));
        }
    }

}
