package de.simagdo.engine.graph.animation;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class AnimatedVertex {

    public Vector3f position;
    public Vector2f textCoords;
    public Vector3f normal;
    public float[] weights;
    public int[] jointIndices;

    public AnimatedVertex() {
        super();
        this.normal = new Vector3f(0, 0, 0);
    }

}
