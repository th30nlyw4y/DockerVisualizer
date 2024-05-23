package com.th30nlyw4y.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.EventType;
import com.th30nlyw4y.model.StatusUpdate;
import com.th30nlyw4y.model.StatusUpdateType;
import com.th30nlyw4y.ui.ContainersTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class StateManager extends SwingWorker<Object, StatusUpdate> {
    private DockerClient dockerClient;
    private ContainersTableModel cTableModel;
    private final StateManagerCallback stateManagerCallback;
    private final Logger log = LoggerFactory.getLogger(StateManager.class);

    public StateManager(ContainersTableModel cTableModel) {
        this(null, cTableModel);
    }

    public StateManager(DockerClient dockerClient, ContainersTableModel cTableModel) {
        super();
        log.info("Initializing State Manager");
        this.cTableModel = cTableModel;
        this.dockerClient = dockerClient != null ? dockerClient : new DockerConnection().getClient();
        stateManagerCallback = new StateManagerCallback();
    }

    public void initState() {
        log.info("Initializing state");
        List<Container> containerList = dockerClient.listContainersCmd()
            .withShowAll(true)
            .exec();
        log.info("Found {} containers", containerList.size());
        for (Container c : containerList) {
            SwingUtilities.invokeLater(() -> cTableModel.addContainer(c));
        }
    }

    private Container getContainerInfo(String containerId) {
        return dockerClient.listContainersCmd()
            .withShowAll(true)
            .withIdFilter(Collections.singletonList(containerId))
            .exec()
            .getFirst();
    }

    /*
        Swing related methods start here
    */

    @Override
    protected Object doInBackground() {
        try {
            dockerClient.eventsCmd()
                .withEventTypeFilter(EventType.CONTAINER)
                .exec(stateManagerCallback)
                .awaitCompletion();
        } catch (InterruptedException intExc) {
            log.warn("State Manager was interrupted, shutting down");
        }
        return null;
    }

    @Override
    protected void process(List<StatusUpdate> chunks) {
        log.info("Updating table model");
        for (StatusUpdate s : chunks) {
            StatusUpdateType updateType = s.getUpdateType();
            switch (updateType) {
                case Created -> cTableModel.addContainer(s.getContainer());
                case Started, Stopped -> cTableModel.updateContainer(s.getContainer());
                case Removed, Destroyed -> cTableModel.removeContainer(s.getContainerId());
            }
        }
    }

    private class StateManagerCallback extends ResultCallback.Adapter<Event> {
        @Override
        public void onNext(Event evt) {
            String containerId = evt.getId();
            StatusUpdateType updateType = StatusUpdateType.getUpdateTypeFromString(evt.getStatus());
            if (updateType != null) {
                switch (updateType) {
                    case Removed, Destroyed -> publish(new StatusUpdate(updateType, containerId));
                    default -> publish(new StatusUpdate(updateType, getContainerInfo(containerId)));
                }
            }
        }
    }
}
