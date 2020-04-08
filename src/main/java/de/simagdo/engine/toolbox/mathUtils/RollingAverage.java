package de.simagdo.engine.toolbox.mathUtils;

import java.util.LinkedList;

public class RollingAverage {

    private final int max;
    private int count = 0;
    private final LinkedList<Float> values = new LinkedList<>();

    public RollingAverage(int count) {
        this.max = count;
    }

    public void addValues(float value) {
        if (this.count >= this.max) {
            this.values.removeFirst();
        } else {
            this.count++;
        }
        this.values.add(value);
    }

    public float calculate() {
        float total = 0;
        for (Float f : this.values) total += f;
        return total / this.count;
    }

}
