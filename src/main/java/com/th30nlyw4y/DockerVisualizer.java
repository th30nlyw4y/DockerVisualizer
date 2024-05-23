package com.th30nlyw4y;

import com.github.dockerjava.api.DockerClient;
import com.th30nlyw4y.docker.DockerConnection;
import com.th30nlyw4y.docker.LogStreamer;
import com.th30nlyw4y.docker.StateManager;
import com.th30nlyw4y.model.ControlButtonType;
import com.th30nlyw4y.ui.ContainersPanel;
import com.th30nlyw4y.ui.ContainersTableModel;
import com.th30nlyw4y.ui.ControlButtonsPanel;
import com.th30nlyw4y.ui.LogPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public final class DockerVisualizer extends JFrame {
    private LogStreamer logStreamer;
    private final DockerClient dockerClient;
    private final StateManager stateManager;
    private final ContainersPanel containersPanel;
    private final LogPanel logPanel;
    private final ControlButtonsPanel controlButtonsPanel;
    private final Logger log = LoggerFactory.getLogger(DockerVisualizer.class);

    public DockerVisualizer() {
        this(null);
    }

    public DockerVisualizer(DockerClient dockerClient) {
        super();
        initFrame();

        // Prepare UI components
        containersPanel = new ContainersPanel();
        logPanel = new LogPanel();
        controlButtonsPanel = new ControlButtonsPanel();

        // Init `backend` services
        this.dockerClient = dockerClient != null ? dockerClient : new DockerConnection().getClient();
        stateManager = new StateManager(
            dockerClient, (ContainersTableModel) containersPanel.getTableModel()
        );

        // Set up action listeners
        initButtonListeners();
        initContainersPanelListeners();

        // Put UI elements together
        add(containersPanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.EAST);
        add(controlButtonsPanel, BorderLayout.SOUTH);
    }

    private void initFrame() {
        // Frame settings
        setTitle("Docker Visualizer");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initButtonListeners() {
        // Control buttons
        controlButtonsPanel.addButtonListener(ControlButtonType.START, e -> startButtonHandler());
        controlButtonsPanel.addButtonListener(ControlButtonType.STOP, e -> stopButtonHandler());
        controlButtonsPanel.addButtonListener(ControlButtonType.LOGS, e -> logsButtonHandler());
        // Log panel close button
        logPanel.addCloseButtonListener(e -> closeLogPanelButtonHandler());
    }

    private void initContainersPanelListeners() {
        // For now only used to update buttons state according to selected container's state change
        containersPanel.addTableModelListener(e -> {
            if (containersPanel.isAffectedByUpdate(e.getFirstRow(), e.getLastRow()))
                refreshControlButtons();
        });
        containersPanel.addSelectionListener(e -> refreshControlButtons());
    }

    private void startButtonHandler() {
        log.debug("Handling Start button event");
        String containerId = containersPanel.getSelectedContainerId();
        dockerClient.startContainerCmd(containerId).exec();
    }

    private void stopButtonHandler() {
        log.debug("Handling Stop button event");
        String containerId = containersPanel.getSelectedContainerId();
        // Close log streaming, if we're going to stop container
        if (logStreamer != null && logStreamer.isCurrentlyStreamed(containerId)) {
            logStreamer.stopStreaming();
            logPanel.setInvisible();
        }
        dockerClient.stopContainerCmd(containerId).exec();
    }

    private void logsButtonHandler() {
        log.debug("Handling Logs button event");
        String containerId = containersPanel.getSelectedContainerId();
        if (logStreamer != null) logStreamer.stopStreaming(); // Stop previous streaming
        logStreamer = new LogStreamer(logPanel.getLogArea(), containerId);
        logStreamer.startStreaming();
        logPanel.setVisible();
    }

    private void closeLogPanelButtonHandler() {
        log.debug("Handling Close log panel button event");
        logStreamer.stopStreaming();
        logPanel.setInvisible();
    }

    private void refreshControlButtons() {
        log.debug("Handling Table selection change event");
        String selectedContainerId = containersPanel.getSelectedContainerId();
        if (selectedContainerId == null) {
            controlButtonsPanel.setDisabled(ControlButtonType.START, ControlButtonType.STOP, ControlButtonType.LOGS);
        } else {
            if (containersPanel.isRunning(selectedContainerId)) {
                controlButtonsPanel.setEnabled(ControlButtonType.STOP, ControlButtonType.LOGS);
                controlButtonsPanel.setDisabled(ControlButtonType.START);
            } else {
                controlButtonsPanel.setEnabled(ControlButtonType.START);
                controlButtonsPanel.setDisabled(ControlButtonType.STOP, ControlButtonType.LOGS);
            }
        }
    }

    public void start() {
        setVisible(true);
        new Thread(stateManager::initState, "InitStateThread").start();
        stateManager.execute();
    }

    public static void main(String[] args) {
        DockerVisualizer dv = new DockerVisualizer();
        dv.start();
    }
}

