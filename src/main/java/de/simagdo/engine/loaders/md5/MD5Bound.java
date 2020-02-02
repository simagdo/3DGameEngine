package de.simagdo.engine.loaders.md5;

import org.joml.Vector3f;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MD5Bound {

    private static final Pattern PATTERN_BOUND = Pattern.compile("\\s*" + MD5Utils.VECTOR3_REGEXP + "\\s*" + MD5Utils.VECTOR3_REGEXP + ".*");
    private Vector3f minBound;
    private Vector3f maxBound;

    public Vector3f getMinBound() {
        return this.minBound;
    }

    public void setMinBound(Vector3f minBound) {
        this.minBound = minBound;
    }

    public Vector3f getMaxBound() {
        return this.maxBound;
    }

    public void setMaxBound(Vector3f maxBound) {
        this.maxBound = maxBound;
    }

    public static MD5Bound parseLine(String line) {
        MD5Bound result = null;
        Matcher matcher = PATTERN_BOUND.matcher(line);
        if (matcher.matches()) {
            result = new MD5Bound();
            float x = Float.parseFloat(matcher.group(1));
            float y = Float.parseFloat(matcher.group(2));
            float z = Float.parseFloat(matcher.group(3));
            result.setMinBound(new Vector3f(x, y, z));

            x = Float.parseFloat(matcher.group(4));
            y = Float.parseFloat(matcher.group(5));
            z = Float.parseFloat(matcher.group(6));
            result.setMaxBound(new Vector3f(x, y, z));
        }
        return result;
    }

    @Override
    public String toString() {
        return "[minBound: " + this.minBound + ", maxBound: " + this.maxBound + "]";
    }
}
