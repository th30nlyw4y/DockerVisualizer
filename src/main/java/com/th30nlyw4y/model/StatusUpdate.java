package com.th30nlyw4y.model;

import com.github.dockerjava.api.model.Container;

public class StatusUpdate {
    private StatusUpdateType updateType;
    private Container container;

    public StatusUpdate(StatusUpdateType updateType, Container container) {
        this.updateType = updateType;
        this.container = container;
    }

    public StatusUpdateType getUpdateType() {
        return updateType;
    }

    public Container getContainer() {
        return container;
    }
}
