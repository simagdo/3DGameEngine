package de.simagdo.engine.loaders.md5;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MD5JointData {

    private static final String PARENT_INDEX_REGEXP = "([-]?\\d+)";
    private static final String NAME_REGEXP = "\\\"([^\\\"]+)\\\"";
    private static final String JOINT_REGEXP = "\\s*" + NAME_REGEXP + "\\s*" + PARENT_INDEX_REGEXP + "\\s*"
            + MD5Utils.VECTOR3_REGEXP + "\\s*" + MD5Utils.VECTOR3_REGEXP + ".*";
    private static final Pattern PATTERN_JOINT = Pattern.compile(JOINT_REGEXP);
    private String name;
    private int parentIndex;
    private Vector3f position;
    private Quaternionf orientation;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentIndex() {
        return this.parentIndex;
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Quaternionf getOrientation() {
        return this.orientation;
    }

    public void setOrientation(Vector3f vec) {
        this.orientation = MD5Utils.calculateQuaternion(vec);
    }

    public static MD5JointData parseLine(String line) {
        MD5JointData result = null;
        Matcher matcher = PATTERN_JOINT.matcher(line);
        if (matcher.matches()) {
            result = new MD5JointData();
            result.setName(matcher.group(1));
            result.setParentIndex(Integer.parseInt(matcher.group(2)));
            float x = Float.parseFloat(matcher.group(3));
            float y = Float.parseFloat(matcher.group(4));
            float z = Float.parseFloat(matcher.group(5));
            result.setPosition(new Vector3f(x, y, z));

            x = Float.parseFloat(matcher.group(6));
            y = Float.parseFloat(matcher.group(7));
            z = Float.parseFloat(matcher.group(8));
            result.setOrientation(new Vector3f(x, y, z));
        }
        return result;
    }

    @Override
    public String toString() {
        return "[name: " + this.name + ", parentIndex: " + this.parentIndex + ", position: " + this.position + ", orientation: " + this.orientation + "]";
    }

}
