package de.simagdo.engine.loaders.md5;

import org.joml.Vector3f;

import java.util.List;

public class VertexInfo {

    public Vector3f position;
    public Vector3f normal;

    public VertexInfo(Vector3f position) {
        this.position = position;
        this.normal = new Vector3f(0, 0, 0);
    }

    public VertexInfo() {
        this.position = new Vector3f();
        this.normal = new Vector3f();
    }

    public static float[] toPositionsArr(List<VertexInfo> list) {
        int length = list != null ? list.size() * 3 : 0;
        float[] result = new float[length];
        int i = 0;
        for (VertexInfo v : list) {
            result[i] = v.position.x;
            result[i + 1] = v.position.y;
            result[i + 2] = v.position.z;
            i += 3;
        }
        return result;
    }

    public static float[] toNormalArr(List<VertexInfo> list) {
        int length = list != null ? list.size() * 3 : 0;
        float[] result = new float[length];
        int i = 0;
        for (VertexInfo v : list) {
            result[i] = v.normal.x;
            result[i + 1] = v.normal.y;
            result[i + 2] = v.normal.z;
            i += 3;
        }
        return result;
    }

}
