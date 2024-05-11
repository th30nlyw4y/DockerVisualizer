package com.th30nlyw4y.model;

public enum StatusUpdateType {
    Created("create"),
    Started("start"),
    Stopped("die"),
    Removed("remove");

    private String dockerEventName;

    StatusUpdateType(String dockerEventName) {
        this.dockerEventName = dockerEventName;
    }

    public static StatusUpdateType getUpdateTypeFromString(String eventName) {
        for (StatusUpdateType t : values()) {
            if (t.dockerEventName.equals(eventName)) {
                return t;
            }
        }
        return null;
    }
}
