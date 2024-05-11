package com.th30nlyw4y.model;

import com.github.dockerjava.api.model.Container;

public class StatusUpdate {
    private StatusUpdateType updateType;
    private Container container;
    private String containerId;

    public StatusUpdate(StatusUpdateType updateType, Container container) {
        this.updateType = updateType;
        this.container = container;
    }

    public StatusUpdate(StatusUpdateType updateType, String containerId) {
        this.updateType = updateType;
        this.containerId = containerId;
    }

    public StatusUpdateType getUpdateType() {
        return updateType;
    }

    public Container getContainer() {
        return container;
    }

    public String getContainerId() {
        return containerId != null ? containerId : container.getId();
    }
}
