package de.simagdo.engine.graph.camera;

public interface ICameraControls {

    public float getZoomInput();

    public float getPitchInput();

    public float getYawInput();

    public boolean goRight();

    public boolean goLeft();

    public boolean goForwards();

    public boolean goBackwards();

}
