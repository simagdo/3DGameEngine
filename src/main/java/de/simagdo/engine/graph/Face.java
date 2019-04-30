package de.simagdo.engine.graph;

public class Face {

    //List of idxGroup groups for a Face Triangle (3 vertices per Face)
    private IdxGroup[] idxGroups;

    public Face(String v1, String v2, String v3) {
        this.idxGroups = new IdxGroup[3];
        this.idxGroups[0] = parseLine(v1);
        this.idxGroups[1] = parseLine(v2);
        this.idxGroups[2] = parseLine(v3);
    }

    private IdxGroup parseLine(String line) {
        IdxGroup idxGroup = new IdxGroup();

        String[] lineTokens = line.split("/");
        int length = lineTokens.length;
        idxGroup.setIdxPos(Integer.parseInt(lineTokens[0]) - 1);

        if (length > 1) {
            //It can be empty if the Obj does not define text coords
            String textCoord = lineTokens[1];
            idxGroup.setIdxTextCoord(textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : idxGroup.getNoValue());
        }

        return idxGroup;
    }

    public IdxGroup[] getFaceVertexIndices() {
        return this.idxGroups;
    }
}