package com.th30nlyw4y;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.th30nlyw4y.docker.StateMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.time.Duration;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class DockerVisualizer {
    private JFrame window;
    private TableModel containersTableModel;
    private DockerClient dockerClient;
    private StateMonitor stateMonitor;
    private final String[] TABLE_COLUMNS = {"Id", "Image", "State"};
    private final Logger log;

    public DockerVisualizer() {
        // Init logging
        log = LoggerFactory.getLogger(DockerVisualizer.class);
        // Docker



        stateMonitor = new StateMonitor(dockerClient, containersTableModel);
        log.info("Launching state monitor");
        stateMonitor.execute();

        setVisible(true);
    }

    private void initUI() {
        // Set up main application frame
        this.window = new JFrame("Docker Visualizer");
        window.setSize(400, 300);
        window.setDefaultCloseOperation(EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setLayout(new BorderLayout());

        // Set up the containers table (and its model)
        this.containersTableModel = new DefaultTableModel();
        ((DefaultTableModel) containersTableModel).setColumnIdentifiers(TABLE_COLUMNS);
        JTable containersTable = new JTable(containersTableModel);
        JScrollPane tableScroll = new JScrollPane(containersTable);

        // Set buttons and their event listeners
        JPanel buttonsPane = new JPanel();
        JButton startButton = new JButton("Start");
        startButton.addActionListener(evt -> {
            int selectedRow = containersTable.getSelectedRow();
            if (selectedRow != -1) {
                String containerId = (String) containersTable.getValueAt(selectedRow, 0);
                try {
                    dockerClient.startContainerCmd(containerId).exec();
                } catch (Exception e) {
                    log.warn("Failed to start container {}. Reason: {}", containerId, e.getMessage());
                }
            }
        });
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(evt -> {
            int selectedRow = containersTable.getSelectedRow();
            if (selectedRow != -1) {
                String containerId = (String) containersTable.getValueAt(selectedRow, 0);
                try {
                    dockerClient.stopContainerCmd(containerId).exec();
                } catch (Exception e) {
                    log.warn("Failed to stop container {}. Reason: {}", containerId, e.getMessage());
                }
            }
        });
        JButton logsButton = new JButton("Logs");
        logsButton.addActionListener(e -> log.warn("Not implemented!"));
        buttonsPane.setLayout(new GridLayout(1, 3));
        buttonsPane.add(startButton);
        buttonsPane.add(stopButton);
        buttonsPane.add(logsButton);

        // Finalize
        this.window.add(tableScroll, BorderLayout.CENTER);
        this.window.add(buttonsPane, BorderLayout.SOUTH);
        this.window.setVisible(true);
    }

    private void initDockerClient() {
        log.info("Initializing docker client");
        DockerClientConfig cfg = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build();
        DockerHttpClient http = new ApacheDockerHttpClient.Builder()
            .dockerHost(cfg.getDockerHost())
            .connectionTimeout(Duration.ofSeconds(10))
            .responseTimeout(Duration.ofSeconds(10))
            .build();
        this.dockerClient = DockerClientImpl.getInstance(cfg, http);
        log.debug("Done");
    }

    public static void main(String[] args) {

    }
}

