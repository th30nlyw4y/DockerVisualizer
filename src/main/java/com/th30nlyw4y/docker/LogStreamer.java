package com.th30nlyw4y.docker;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LogStreamer extends SwingWorker<Integer, String> {
    private String containerId;
    private DockerConnection dockerConn;
    private JFrame logWindow;
    private JTextArea logArea;
    private Logger log = LoggerFactory.getLogger(LogStreamer.class);

    public LogStreamer(String containerId) {
        super();

        this.dockerConn = new DockerConnection();
        this.containerId = containerId;

        logWindow = new JFrame(String.format("%s logs", containerId));
        logWindow.setSize(400, 300);
        logWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        logWindow.setLocationRelativeTo(null);
        logWindow.setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);

        JScrollPane logsScroll = new JScrollPane(logArea);

        logWindow.add(logsScroll, BorderLayout.CENTER);

        logWindow.setVisible(true);
    }

    @Override
    protected Integer doInBackground() throws Exception {
        log.info("Starting log streaming for container {}", containerId);
        try {
            dockerConn.getClient().logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)
                .withTailAll()
                .exec(new ResultCallback.Adapter<Frame>() {
                    @Override
                    public void onNext(Frame f) {
                        publish(f.toString());
                    }
                }).awaitCompletion();
        } catch (Exception e) {
            log.warn("Exception occurred while streaming logs: {}", e.getMessage());
        } finally {
            dockerConn.getClient().close();
        }
        return 0;
    }

    @Override
    protected void process(List<String> chunks) {
        log.info("New log line detected for container {}", containerId);
        for (String c : chunks) {
            logArea.append(c + "\n");
        }
    }
}
