package de.simagdo.engine.inputsOutputs.stateControl;

public class EmptyState implements State {

    @Override
    public int getPriority() {
        return 1000;
    }
}
