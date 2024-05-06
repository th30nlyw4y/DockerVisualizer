package com.th30nlyw4y.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StateMonitor extends SwingWorker<Integer, Map<String, Container>> {
    private DockerClient dockerClient;
    private DefaultTableModel containersTableModel;
    private Map<String, Container> state;
    private final Lock stateLock = new ReentrantLock();
    private final Logger log = LoggerFactory.getLogger(StateMonitor.class);

    public StateMonitor(DockerClient dockerClient, DefaultTableModel containersTableModel) {
        log.info("Initializing state monitor");
        this.dockerClient = dockerClient;
        this.containersTableModel = containersTableModel;
        initState();
        log.info("Done");
    }

    public void startContainer(String id) {
        try {
            dockerClient.startContainerCmd(id).exec();
        } catch (Exception e) {
            log.error("Failed to start container with id {}: {}", id, e.getMessage());
        }
    }

    public void stopContainer(String id) {
        try {
            dockerClient.stopContainerCmd(id).exec();
        } catch (Exception e) {
            log.error("Failed to stop container with id {}: {}", id, e.getMessage());
        }
    }

    private void initState() {
        log.info("Getting containers list");
        List<Container> containerList = dockerClient.listContainersCmd()
            .withShowAll(true)
            .exec();

        this.state = new LinkedHashMap<>();
        for (var c : containerList) {
            log.debug("Adding container {} to state", c.getId());
            this.state.put(c.getId(), c);
        }
        log.debug("State after initialization: {}", state);
        log.info("Done");
    }

    private Container getContainerState(String containerId) {
        return dockerClient.listContainersCmd()
            .withShowAll(true)
            .withIdFilter(Collections.singletonList(containerId))
            .exec()
            .getFirst();
    }

    private void monitorEvents() {
        log.info("Starting Docker events monitoring");
        try {
            dockerClient.eventsCmd()
                .withEventTypeFilter(EventType.CONTAINER)
                .exec(new ResultCallback.Adapter<Event>() {
                    @Override
                    public void onNext(Event evt) {
                        String containerId = evt.getId();
                        String[] imgSplit = evt.getFrom().split("/");
                        String containerImg = imgSplit[imgSplit.length - 1];
                        String status = evt.getStatus();

                        stateLock.lock();
                        switch (status) {
                            case "create":
                                log.info("Container '{}' created", containerImg);
                                state.put(containerId, getContainerState(containerId));
                                break;
                            case "start":
                                log.info("Container '{}' started", containerImg);
                                state.put(containerId, getContainerState(containerId));
                                break;
                            case "die":
                                log.info("Container '{}' stopped", containerImg);
                                state.put(containerId, getContainerState(containerId));
                                break;
                            case "remove":
                                log.info("Container '{}' removed", containerImg);
                                state.remove(containerId);
                        }
                        stateLock.unlock();
                        publish(state);
                    }
                }).awaitCompletion();
        } catch (InterruptedException intExc) {
            try {
                dockerClient.close();
            } catch (IOException IOExc) {
                log.error("Failed to close docker connection: {}", IOExc.getMessage());
            }
        } finally {
            stateLock.unlock();
        }
    }

    /*
        Swing related methods start here
    */

    @Override
    protected Integer doInBackground() throws Exception {
        publish(state);
        monitorEvents();
        return 0;
    }

    @Override
    protected void process(List<Map<String, Container>> chunks) {
        while (containersTableModel.getRowCount() > 0) {
            containersTableModel.removeRow(0);
        }
        for (Map.Entry<String, Container> e : state.entrySet()) {
            String[] rowData = {e.getKey(), e.getValue().getImage(), e.getValue().getState()};
            containersTableModel.addRow(rowData);
        }
    }
}
