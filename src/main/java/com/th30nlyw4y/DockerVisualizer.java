package com.th30nlyw4y;

import com.github.dockerjava.api.DockerClient;
import com.th30nlyw4y.docker.DockerConnection;
import com.th30nlyw4y.model.ControlButtonType;
import com.th30nlyw4y.ui.ContainersPanel;
import com.th30nlyw4y.ui.ControlButtonsPanel;
import com.th30nlyw4y.ui.LogPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class DockerVisualizer extends JFrame {
    private DockerClient dockerClient;
    private ContainersPanel containersPanel;
    private LogPanel logPanel;
    private ControlButtonsPanel buttonsPanel;
    static final Logger log = LoggerFactory.getLogger(DockerVisualizer.class);

    public DockerVisualizer() {
        super();
        initFrame();

        // Init Docker client
        dockerClient = new DockerConnection().getClient();

        // Prepare UI components
        containersPanel = new ContainersPanel();
        logPanel = new LogPanel();
        buttonsPanel = new ControlButtonsPanel();

        // Set up action listeners
        initControlButtonPanelListeners();
        initContainersPanelListeners();

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

    private void initControlButtonPanelListeners() {
        buttonsPanel.getButton(ControlButtonType.START)
            .addActionListener(e -> startButtonHandler());
        buttonsPanel.getButton(ControlButtonType.STOP)
            .addActionListener(e -> stopButtonHandler());
        buttonsPanel.getButton(ControlButtonType.LOGS)
            .addActionListener(e -> logsButtonHandler());
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
        DockerVisualizer.log.debug("Handling Start button event");
        String containerId = containersPanel.getSelectedContainerId();
        dockerClient.startContainerCmd(containerId).exec();
    }

    private void stopButtonHandler() {
        DockerVisualizer.log.debug("Handling Stop button event");
        String containerId = containersPanel.getSelectedContainerId();
        // Close log streaming, if we're going to stop container
        logPanel.stopIfCurrentlyStreamed(containerId);
        dockerClient.stopContainerCmd(containerId).exec();
    }

    private void logsButtonHandler() {
        DockerVisualizer.log.debug("Handling Logs button event");
        String containerId = containersPanel.getSelectedContainerId();
        logPanel.startLogStreamingAndShow(containerId);
    }

    private void refreshControlButtons() {
        DockerVisualizer.log.debug("Handling Table selection change event");
        String selectedContainerId = containersPanel.getSelectedContainerId();
        if (selectedContainerId == null) {
            buttonsPanel.setDisabled(ControlButtonType.START, ControlButtonType.STOP, ControlButtonType.LOGS);
        } else {
            if (containersPanel.isRunning(selectedContainerId)) {
                buttonsPanel.setEnabled(ControlButtonType.STOP, ControlButtonType.LOGS);
                buttonsPanel.setDisabled(ControlButtonType.START);
            } else {
                buttonsPanel.setEnabled(ControlButtonType.START);
                buttonsPanel.setDisabled(ControlButtonType.STOP, ControlButtonType.LOGS);
            }
        }
    }

    public void start() {
        setVisible(true);
    }

    public static void main(String[] args) {
        DockerVisualizer dv = new DockerVisualizer();
        dv.start();
    }
}

