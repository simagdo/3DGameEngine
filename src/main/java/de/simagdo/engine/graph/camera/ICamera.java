package de.simagdo.engine.graph.camera;

import de.simagdo.engine.toolbox.misc.Listener;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ICamera {

    public void move(float delta);

    public Vector3f getPosition();

    public Matrix4f getViewMatrix();

    public Matrix4f getProjectionMatrix();

    public Matrix4f getProjectionViewMatrix();

    public void addMoveListener(Listener listener);

}
