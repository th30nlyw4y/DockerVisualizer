package com.th30nlyw4y.model;

import com.github.dockerjava.api.model.Container;

public class StatusUpdate {
    private StatusUpdateType updateType;
    private String containerId;
    private Container container;

    public StatusUpdate(StatusUpdateType updateType, String containerId) {
        this(updateType, containerId, null);
    }

    public StatusUpdate(StatusUpdateType updateType, Container container) {
        this(updateType, container.getId(), container);
    }

    private StatusUpdate(StatusUpdateType updateType, String containerId, Container container) {
        this.updateType = updateType;
        this.containerId = containerId;
        this.container = container;
    }

    public StatusUpdateType getUpdateType() {
        return updateType;
    }

    public String getContainerId() {
        return containerId;
    }

    public Container getContainer() {
        return container;
    }
}
