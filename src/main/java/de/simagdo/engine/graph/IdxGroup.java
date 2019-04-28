package de.simagdo.engine.graph;

public class IdxGroup {

    private static final int NO_VALUE = -1;
    private int idxPos;
    private int idxTextCoord;
    private int idxVecNormal;

    public IdxGroup() {
        this.idxPos = NO_VALUE;
        this.idxTextCoord = NO_VALUE;
        this.idxVecNormal = NO_VALUE;
    }

    public static int getNoValue() {
        return NO_VALUE;
    }

    public int getIdxPos() {
        return idxPos;
    }

    public void setIdxPos(int idxPos) {
        this.idxPos = idxPos;
    }

    public int getIdxTextCoord() {
        return idxTextCoord;
    }

    public void setIdxTextCoord(int idxTextCoord) {
        this.idxTextCoord = idxTextCoord;
    }

    public int getIdxVecNormal() {
        return idxVecNormal;
    }

    public void setIdxVecNormal(int idxVecNormal) {
        this.idxVecNormal = idxVecNormal;
    }
}
