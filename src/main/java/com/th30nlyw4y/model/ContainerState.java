package com.th30nlyw4y.model;

public enum ContainerState {
    CREATED("created"),
    RESTARTING("restarting"),
    RUNNING("running"),
    PAUSED("paused"),
    EXITED("exited"),
    DEAD("dead");

    private final String value;

    ContainerState(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
