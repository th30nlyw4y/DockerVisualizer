package com.th30nlyw4y;

import com.github.dockerjava.api.DockerClient;
import com.th30nlyw4y.docker.DockerConnection;
import com.th30nlyw4y.docker.LogStreamer;
import com.th30nlyw4y.docker.StateManager;
import com.th30nlyw4y.ui.ButtonsPanel;
import com.th30nlyw4y.ui.ContainersPanel;
import com.th30nlyw4y.ui.LogPanel;
import com.th30nlyw4y.model.ButtonType;
import com.th30nlyw4y.model.ContainerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class DockerVisualizer extends JFrame {
    private DockerClient dockerClient;
    private StateManager stateManager;
    private LogStreamer logStreamer;
    private ContainersPanel containersPanel;
    private LogPanel logPanel;
    private ButtonsPanel buttonsPanel;
    static final Logger log = LoggerFactory.getLogger(DockerVisualizer.class);

    public DockerVisualizer() {
        super();
        initFrame();

        // Create Docker client
        dockerClient = new DockerConnection().getClient();

        // Prepare UI components
        containersPanel = new ContainersPanel(ContainerProperty.Image, ContainerProperty.State);
        logPanel = new LogPanel();
        buttonsPanel = new ButtonsPanel();

        // Set up action listeners
        initActionListeners();

        // Init state manager
        // Also populates the table with initial values
        stateManager = new StateManager(dockerClient, containersPanel.getTable());

        // Put everything together
        add(containersPanel, BorderLayout.CENTER);
        add(logPanel, BorderLayout.EAST);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void initFrame() {
        // Frame settings
        setTitle("Docker Visualizer");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initActionListeners() {
        // Table actions
        containersPanel.getTable().getSelectionModel()
            .addListSelectionListener(e -> tableSelectionChanged());
        // Buttons actions
        buttonsPanel.getButton(ButtonType.START_BUTTON)
            .addActionListener(e -> startButtonHandler());
        buttonsPanel.getButton(ButtonType.STOP_BUTTON)
            .addActionListener(e -> stopButtonHandler());
        buttonsPanel.getButton(ButtonType.LOGS_BUTTON)
            .addActionListener(e -> logsButtonHandler());

    }

    private void startButtonHandler() {
        DockerVisualizer.log.debug("Handling Start button event");
        String selectedContainerId = containersPanel.getSelectedContainerId();
        dockerClient.startContainerCmd(selectedContainerId).exec();
    }

    private void stopButtonHandler() {
        DockerVisualizer.log.debug("Handling Stop button event");
        String selectedContainerId = containersPanel.getSelectedContainerId();
        if (logStreamer != null && logStreamer.isCurrentlyStreamed(selectedContainerId)) {
            DockerVisualizer.log.info(
                "Container {} scheduled for stop. Cancelling log streaming",
                selectedContainerId
            );
            logStreamer.cancel(true);
            logPanel.setInvisible();
        }
        dockerClient.stopContainerCmd(selectedContainerId).exec();
    }

    private void logsButtonHandler() {
        DockerVisualizer.log.debug("Handling Logs button event");
        String selectedContainerId = containersPanel.getSelectedContainerId();
        if (logStreamer != null) {
            logStreamer.cancel(true);
        }
        logStreamer = new LogStreamer(dockerClient, logPanel, selectedContainerId);
        logPanel.clear();
        logStreamer.execute();
    }

    private void tableSelectionChanged() {
        DockerVisualizer.log.info("Handling Table selection changed event");
        String selectedContainerId = containersPanel.getSelectedContainerId();
        if (selectedContainerId == null) {
            buttonsPanel.setDisabled(ButtonType.START_BUTTON, ButtonType.STOP_BUTTON, ButtonType.LOGS_BUTTON);
        } else {
            if (stateManager.isRunning(selectedContainerId)) {
                buttonsPanel.setEnabled(ButtonType.STOP_BUTTON, ButtonType.LOGS_BUTTON);
                buttonsPanel.setDisabled(ButtonType.START_BUTTON);
            } else {
                buttonsPanel.setEnabled(ButtonType.START_BUTTON);
                buttonsPanel.setDisabled(ButtonType.STOP_BUTTON, ButtonType.LOGS_BUTTON);
            }
        }
    }

    public void start() {
        setVisible(true);
        stateManager.execute();
    }

    public static void main(String[] args) {
        DockerVisualizer dv = new DockerVisualizer();
        dv.start();
    }
}

