package com.th30nlyw4y;

import com.github.dockerjava.api.DockerClient;
import com.th30nlyw4y.docker.DockerConnection;
import com.th30nlyw4y.docker.LogStreamer;
import com.th30nlyw4y.docker.StateManager;
import com.th30nlyw4y.ui.ButtonsPanel;
import com.th30nlyw4y.ui.ContainersPanel;
import com.th30nlyw4y.ui.LogPanel;
import com.th30nlyw4y.utils.ButtonType;
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
    private final String[] PARAMS_TO_SHOW = {"Id", "Image", "Status"};
    static final Logger log = LoggerFactory.getLogger(DockerVisualizer.class);

    public DockerVisualizer() {
        super();
        initFrame();

        // Create Docker client
        dockerClient = new DockerConnection().getClient();

        // Prepare UI components
        containersPanel = new ContainersPanel(PARAMS_TO_SHOW);
        logPanel = new LogPanel();
        buttonsPanel = new ButtonsPanel();
        initButtonListeners();

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
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initButtonListeners() {
        buttonsPanel.getButton(ButtonType.START_BUTTON)
            .addActionListener(e -> startButtonHandler());
        buttonsPanel.getButton(ButtonType.STOP_BUTTON)
            .addActionListener(e -> stopButtonHandler());
        buttonsPanel.getButton(ButtonType.LOGS_BUTTON)
            .addActionListener(e -> logsButtonHandler());
    }

    private void startButtonHandler() {
        DockerVisualizer.log.info("Handling Start button event");
        String selectedContainerId = containersPanel.getSelectedContainerId();
        if (selectedContainerId == null) return;
        dockerClient.startContainerCmd(selectedContainerId).exec();
    }

    private void stopButtonHandler() {
        DockerVisualizer.log.info("Handling Stop button event");
        String selectedContainerId = containersPanel.getSelectedContainerId();
        if (selectedContainerId == null) return;
        if (logStreamer != null && logStreamer.isCurrentlyStreamed(selectedContainerId)) {
            DockerVisualizer.log.info(
                "Container {} scheduled for stop. Cancelling log streaming",
                selectedContainerId
            );
            logStreamer.cancel(true);
            logPanel.setVisible(false);
            validate();
        }
        dockerClient.stopContainerCmd(selectedContainerId).exec();
    }

    private void logsButtonHandler() {
        DockerVisualizer.log.info("Handling Logs button event");
        if (logStreamer != null) {
            logStreamer.cancel(true);
        }
        String selectedContainerId = containersPanel.getSelectedContainerId();
        logStreamer = new LogStreamer(dockerClient, logPanel, selectedContainerId);
        logStreamer.execute();
        logPanel.setVisible(true);
        validate();
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

