package de.simagdo.engine.loaders.md5;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MD5BaseFrame {

    private List<MD5BaseFrameData> frameDataList;

    public List<MD5BaseFrameData> getFrameDataList() {
        return this.frameDataList;
    }

    public void setFrameDataList(List<MD5BaseFrameData> frameDataList) {
        this.frameDataList = frameDataList;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("base frame [" + System.lineSeparator());
        for (MD5BaseFrameData frameData : this.frameDataList) {
            str.append(frameData).append(System.lineSeparator());
        }
        str.append("]").append(System.lineSeparator());
        return str.toString();
    }

    public static MD5BaseFrame parse(List<String> blockBody) {
        MD5BaseFrame result = new MD5BaseFrame();

        List<MD5BaseFrameData> frameInfoList = new ArrayList<>();
        result.setFrameDataList(frameInfoList);

        for (String line : blockBody) {
            MD5BaseFrameData frameInfo = MD5BaseFrameData.parseLine(line);
            if (frameInfo != null) {
                frameInfoList.add(frameInfo);
            }
        }

        return result;
    }

}
