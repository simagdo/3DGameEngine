package de.simagdo.engine.loaders.md5;

import java.util.ArrayList;
import java.util.List;

public class MD5Hierarchy {

    private List<MD5HierarchyData> hierarchyDataList;

    public List<MD5HierarchyData> getHierarchyDataList() {
        return this.hierarchyDataList;
    }

    public void setHierarchyDataList(List<MD5HierarchyData> hierarchyDataList) {
        this.hierarchyDataList = hierarchyDataList;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("hierarchy [" + System.lineSeparator());
        for (MD5HierarchyData hierarchyData : hierarchyDataList) {
            str.append(hierarchyData).append(System.lineSeparator());
        }
        str.append("]").append(System.lineSeparator());
        return str.toString();
    }

    public static MD5Hierarchy parse(List<String> blockBody) {
        MD5Hierarchy result = new MD5Hierarchy();
        List<MD5HierarchyData> hierarchyDataList = new ArrayList<>();
        result.setHierarchyDataList(hierarchyDataList);
        for (String line : blockBody) {
            MD5HierarchyData hierarchyData = MD5HierarchyData.parseLine(line);
            if (hierarchyData != null) {
                hierarchyDataList.add(hierarchyData);
            }
        }
        return result;
    }

}
