package de.simagdo.engine.loaders.md5;

import java.util.ArrayList;
import java.util.List;

public class MD5BoundInfo {

    private List<MD5Bound> bounds;

    public List<MD5Bound> getBounds() {
        return this.bounds;
    }

    public void setBounds(List<MD5Bound> bounds) {
        this.bounds = bounds;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("bounds [" + System.lineSeparator());
        for (MD5Bound bound : this.bounds) {
            str.append(bound).append(System.lineSeparator());
        }
        str.append("]").append(System.lineSeparator());
        return str.toString();
    }

    public static MD5BoundInfo parse(List<String> blockBody) {
        MD5BoundInfo result = new MD5BoundInfo();
        List<MD5Bound> bounds = new ArrayList<>();
        for (String line : blockBody) {
            MD5Bound bound = MD5Bound.parseLine(line);
            if (bound != null) {
                bounds.add(bound);
            }
        }
        result.setBounds(bounds);
        return result;
    }

}
