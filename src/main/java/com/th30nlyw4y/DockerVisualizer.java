package com.th30nlyw4y;

import com.th30nlyw4y.docker.StateManager;
import com.th30nlyw4y.ui.ButtonsPanel;
import com.th30nlyw4y.ui.ContainersPanel;
import com.th30nlyw4y.ui.LogPanel;
import com.th30nlyw4y.utils.ButtonType;
import com.th30nlyw4y.utils.DockerCmdType;

import javax.swing.*;
import java.awt.*;

public class DockerVisualizer extends JFrame {
    private final StateManager stateManager;
    private final ContainersPanel containersPanel;
    private final LogPanel logPanel;
    private final ButtonsPanel buttonsPanel;
    private final String[] PARAMS_TO_SHOW = {"Id", "Image", "Status"};

    public DockerVisualizer() {
        super();
        initFrame();

        // Prepare UI components
        containersPanel = new ContainersPanel(PARAMS_TO_SHOW);
        logPanel = new LogPanel();
        buttonsPanel = new ButtonsPanel();
        initButtonListeners();

        // Init state manager
        // Also populates the table with initial values
        stateManager = new StateManager(containersPanel.getTable());

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
            .addActionListener(e -> stateManager.executeDockerCmd(DockerCmdType.START_CMD));
        buttonsPanel.getButton(ButtonType.STOP_BUTTON)
            .addActionListener(e -> stateManager.executeDockerCmd(DockerCmdType.STOP_CMD));
        buttonsPanel.getButton(ButtonType.LOGS_BUTTON)
            .addActionListener(e -> stateManager.executeDockerCmd(DockerCmdType.LOGS_CMD));
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

