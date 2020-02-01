package de.simagdo.engine.loaders.md5;

import java.util.ArrayList;
import java.util.List;

public class MD5JointInfo {

    private List<MD5JointData> joints;

    public List<MD5JointData> getJoints() {
        return this.joints;
    }

    public void setJoints(List<MD5JointData> joints) {
        this.joints = joints;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("joints [" + System.lineSeparator());
        for (MD5JointData joint : this.joints) {
            str.append(joint).append(System.lineSeparator());
        }
        str.append("]").append(System.lineSeparator());
        return str.toString();
    }

    public static MD5JointInfo parse(List<String> blockBody) {
        MD5JointInfo result = new MD5JointInfo();
        List<MD5JointData> joints = new ArrayList<>();
        for (String line : blockBody) {
            MD5JointData jointData = MD5JointData.parseLine(line);
            if (jointData != null) {
                joints.add(jointData);
            }
        }
        result.setJoints(joints);
        return result;
    }

}
