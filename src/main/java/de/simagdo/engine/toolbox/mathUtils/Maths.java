package de.simagdo.engine.toolbox.mathUtils;

public class Maths {

    public static float clamp(float number, float min, float max) {
        return Math.min(Math.max(number, min), max);
    }

    public static float cosInterpolate(float a, float b, float blend) {
        double ft = blend * Math.PI;
        float f = (float) ((1f - Math.cos(ft)) * 0.5f);
        return a * (1 - f) + b * f;
    }

}
