package com.th30nlyw4y.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.EventType;
import com.th30nlyw4y.utils.DockerCmdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class StateManager extends SwingWorker<Integer, Integer> {
    private DockerClient dockerClient;
    private JTable cTable;
    private Map<String, Container> state;
    private final Logger log = LoggerFactory.getLogger(StateManager.class);
    private final ReentrantLock stateLock = new ReentrantLock();

    public StateManager(JTable cTable) {
        super();
        log.info("Initializing State Manager");
        this.cTable = cTable;
        this.dockerClient = new DockerConnection().getClient();
        initState();
    }

    private void initState() {
        log.info("Initializing state");
        List<Container> containerList = dockerClient.listContainersCmd()
            .withShowAll(true)
            .exec();
        state = new LinkedHashMap<>();
        for (var c : containerList) {
            state.put(c.getId(), c);

            // Also fill the table model with initial values
            ((DefaultTableModel) cTable.getModel()).addRow(new Object[]{c.getId(), c.getImage(), c.getState()});
        }
        log.debug("Current state: {}", state);
    }

    private Container getContainerInfo(String containerId) {
        return dockerClient.listContainersCmd()
            .withShowAll(true)
            .withIdFilter(Collections.singletonList(containerId))
            .exec()
            .getFirst();
    }

    public void executeDockerCmd(DockerCmdType t) {
        // TODO change column names to enum
        int col = cTable.getColumn("Id").getModelIndex();
        int row = cTable.getSelectedRow();
        if (row == -1) return;
        String selectedContainerId = (String) cTable.getModel().getValueAt(row, col);
        try {
            switch (t) {
                case START_CMD -> {
                    log.info("Executing start command for container {}", selectedContainerId);
                    dockerClient.startContainerCmd(selectedContainerId).exec();
                }
                case STOP_CMD -> {
                    log.info("Executing stop command for container {}", selectedContainerId);
                    dockerClient.stopContainerCmd(selectedContainerId).exec();
                }
                case LOGS_CMD -> {
                    log.info("Executing logs command for container {}", selectedContainerId);
                    LogStreamer ls = new LogStreamer(selectedContainerId);
                    ls.execute();
                }
                default -> log.warn("Unknown command: {}", t);
            }
        } catch (Exception e) {
            log.error("Failed to execute Docker command: {}", e.getMessage());
        }
    }

    /*
        Swing related methods start here
    */

    @Override
    protected Integer doInBackground() throws Exception {
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

                        switch (status) {
                            case "create":
                                log.info("Container '{}' created", containerImg);
                                state.put(containerId, getContainerInfo(containerId));
                                break;
                            case "start":
                                log.info("Container '{}' started", containerImg);
                                state.put(containerId, getContainerInfo(containerId));
                                break;
                            case "die":
                                log.info("Container '{}' stopped", containerImg);
                                state.put(containerId, getContainerInfo(containerId));
                                break;
                            case "remove":
                                log.info("Container '{}' removed", containerImg);
                                state.remove(containerId);
                        }
                        // This is a dummy publish, since we actually redraw the table every time
                        // using shared state
                        publish(1);
                    }
                }).awaitCompletion();
        } catch (InterruptedException intExc) {
            log.warn("State Manager was interrupted, shutting down");
        } finally {
            try {
                dockerClient.close();
            } catch (IOException IOExc) {
                log.error("Failed to close docker connection: {}", IOExc.getMessage());
            }
        }
        return 0;
    }

    @Override
    protected void process(List<Integer> chunks) {
        log.info("Updating table model");
        TableModel tModel = cTable.getModel();
        while (tModel.getRowCount() > 0) {
            ((DefaultTableModel) tModel).removeRow(0);
        }
        // We need this lock here, because SwingWorker modifies `state` in the background thread,
        // which causes iterator to throw exceptions
        stateLock.lock();
        for (Map.Entry<String, Container> e : state.entrySet()) {
            ((DefaultTableModel) tModel).addRow(new String[]{e.getKey(), e.getValue().getImage(), e.getValue().getState()});
        }
        stateLock.unlock();
    }
}
