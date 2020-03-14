package de.simagdo.engine.inputsOutputs.stateControl;

public class QueuedState {

    private final State state;
    private final boolean waitForEndRequest;

    public QueuedState(State state, boolean waitForEndRequest) {
        this.state = state;
        this.waitForEndRequest = waitForEndRequest;
    }

    public State getState() {
        return state;
    }

    public boolean isWaitForEndRequest() {
        return waitForEndRequest;
    }

}
