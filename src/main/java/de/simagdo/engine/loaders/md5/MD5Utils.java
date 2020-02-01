package de.simagdo.engine.loaders.md5;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MD5Utils {

    public static final String FLOAT_REGEXP = "[+.]?\\d*\\.?\\d*";
    public static final String VECTOR3_REGEXP = "\\(\\s*(" + FLOAT_REGEXP + ")\\s*(" + FLOAT_REGEXP + ")\\s*(" + FLOAT_REGEXP + ")\\s*\\)";

    public static Quaternionf calculateQuaternion(Vector3f vector) {
        Quaternionf orientation = new Quaternionf(vector.x, vector.y, vector.z, 0);
        float temp = 1.0f - (orientation.x * orientation.x) - (orientation.y * orientation.y) - (orientation.z * orientation.z);
        if (temp < 0.0f) orientation.w = 0.0f;
        else orientation.w = -(float) (Math.sqrt(temp));
        return orientation;
    }

}
