package com.th30nlyw4y.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class LogStreamer extends SwingWorker<Object, String> {
    private DockerClient dockerClient;
    private JTextArea logArea;
    private String containerId;
    private Logger log = LoggerFactory.getLogger(LogStreamer.class);

    public LogStreamer(JTextArea logArea, String containerId) {
        this(null, logArea, containerId);
    }

    public LogStreamer(DockerClient dockerClient, JTextArea logArea, String containerId) {
        super();
        this.dockerClient = dockerClient != null ? dockerClient : new DockerConnection().getClient();
        this.logArea = logArea;
        this.containerId = containerId;
    }

    public Boolean isCurrentlyStreamed(String containerId) {
        return (this.containerId).equals(containerId);
    }

    public void close() {
        this.cancel(true);
        try {
            dockerClient.close();
        } catch (IOException e) {
            log.error("Failed to close Docker client: {}", e.getMessage());
        }
    }

    @Override
    protected Object doInBackground() {
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
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String s : chunks) {
            logArea.append(s + "\n");
        }
    }
}
