package com.th30nlyw4y.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import com.th30nlyw4y.ui.LogPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;

public class LogStreamer extends SwingWorker<Integer, String> {
    private DockerClient dockerClient;
    private LogPanel logPanel;
    private String containerId;
    private Logger log = LoggerFactory.getLogger(LogStreamer.class);

    public LogStreamer(DockerClient dockerClient, LogPanel logPanel, String containerId) {
        super();
        this.dockerClient = dockerClient;
        this.logPanel = logPanel;
        this.containerId = containerId;
    }

    public Boolean isCurrentlyStreamed(String containerId) {
        return (this.containerId).equals(containerId);
    }

    @Override
    protected Integer doInBackground() {
        log.info("Starting log streaming for container {}", containerId);
        try {
            dockerClient.logContainerCmd(containerId)
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
        } catch (InterruptedException e) {
            log.warn("Interrupted while streaming logs for container {}", containerId);
        }
        return 0;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String s : chunks) {
            logPanel.appendLog(s);
        }
        logPanel.setVisible(true);
        logPanel.getParent().validate();
    }
}
