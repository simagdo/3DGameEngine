package de.simagdo.engine.inputsOutputs.stateControl;

import de.simagdo.engine.inputsOutputs.userInput.UserInput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StateManager {

    private final State defaultState;
    private RegisteredUsers registeredUsers = new RegisteredUsers();
    private State currentState;
    private List<QueuedState> queuedState = new ArrayList<>();

    public StateManager(State defaultState, State initialState) {
        this.defaultState = defaultState;
        this.currentState = initialState;
        this.suggestState(initialState, true);
    }

    public void suggestState(State state, boolean waitForEndRequest) {
        QueuedState queuedState = new QueuedState(state, waitForEndRequest);
        this.sortStateIntoQueue(queuedState);
    }

    public void endState(State state) {
        Iterator<QueuedState> iterator = this.queuedState.iterator();
        while (iterator.hasNext()) {
            QueuedState queuedState = iterator.next();
            if (queuedState.getState() == state) {
                iterator.remove();
                return;
            }
        }
    }

    public State getCurrentState() {
        return this.currentState;
    }

    public void updateState() {
        State nextState = this.getNextState();
        this.cleanQueue();
        if (this.currentState == nextState) return;
        this.switchState(nextState);
    }

    public void registerUser(UserInput user, State... states) {
        for (State state : states) {
            this.registeredUsers.registerUser(state, user);
        }
    }

    public void registerKeyboardUser(UserInput user, State... states) {
        for (State state : states) {
            this.registeredUsers.registerKeyboardUser(state, user);
        }
    }

    public void registerMouseUser(UserInput user, State... states) {
        for (State state : states) {
            this.registeredUsers.registerMouseUser(state, user);
        }
    }

    private void switchState(State newState) {
        this.registeredUsers.endState(this.currentState);
        this.currentState = newState;
        this.registeredUsers.initState(this.currentState);
    }

    private State getNextState() {
        if (this.queuedState.isEmpty()) return this.defaultState;
        return this.queuedState.get(0).getState();
    }

    private void cleanQueue() {
        this.queuedState.removeIf(queuedState -> !queuedState.isWaitForEndRequest());
    }

    private void sortStateIntoQueue(QueuedState newState) {
        for (int i = 0; i < this.queuedState.size(); i++) {
            if (queuedState.get(i).getState().getPriority() > newState.getState().getPriority()) {
                queuedState.add(i, newState);
                return;
            }
        }
        this.queuedState.add(newState);
    }

}
