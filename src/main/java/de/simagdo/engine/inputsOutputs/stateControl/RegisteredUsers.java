package de.simagdo.engine.inputsOutputs.stateControl;

import de.simagdo.engine.inputsOutputs.userInput.UserInput;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegisteredUsers {

    private Map<State, Set<UserInput>> registeredMouseUsers = new HashMap<>();
    private Map<State, Set<UserInput>> registeredKeyboardUsers = new HashMap<>();

    public void registerMouseUser(State state, UserInput user) {
        this.registerInputUser(state, user, this.registeredMouseUsers);
    }

    public void registerKeyboardUser(State state, UserInput user) {
        this.registerInputUser(state, user, this.registeredKeyboardUsers);
    }

    public void registerUser(State state, UserInput user) {
        this.registerInputUser(state, user, this.registeredMouseUsers);
        this.registerInputUser(state, user, this.registeredKeyboardUsers);
    }

    public void initState(State state) {
        this.enableMouseUsers(state, true);
        this.enableKeyboardUsers(state, true);
    }

    public void endState(State state) {
        this.enableMouseUsers(state, false);
        this.enableKeyboardUsers(state, false);
    }

    private void registerInputUser(State state, UserInput user, Map<State, Set<UserInput>> registeredUsers) {
        Set<UserInput> users = registeredUsers.computeIfAbsent(state, k -> new HashSet<>());
        users.add(user);
    }

    private void enableMouseUsers(State state, boolean enable) {
        Set<UserInput> users = this.registeredMouseUsers.get(state);
        if (users == null) return;

        users.forEach(user -> {
            user.enableMouseUse(enable);
        });

    }

    private void enableKeyboardUsers(State state, boolean enable) {
        Set<UserInput> users = this.registeredKeyboardUsers.get(state);
        if (users == null) return;

        users.forEach(user -> user.enableKeyboardUse(enable));
    }

}
