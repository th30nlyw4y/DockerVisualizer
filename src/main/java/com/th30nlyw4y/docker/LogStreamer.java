package com.th30nlyw4y.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;

public class LogStreamer extends SwingWorker<Object, String> {
    private final DockerClient dockerClient;
    private final JTextArea logArea;
    private final String containerId;
    private final ResultCallback.Adapter<Frame> logStreamCallback;
    private final Logger log = LoggerFactory.getLogger(LogStreamer.class);
    private Boolean currentlyStreaming;

    public LogStreamer(JTextArea logArea, String containerId) {
        this(null, logArea, containerId);
    }

    public LogStreamer(DockerClient dockerClient, JTextArea logArea, String containerId) {
        super();
        this.dockerClient = dockerClient != null ? dockerClient : new DockerConnection().getClient();
        this.logArea = logArea;
        this.containerId = containerId;
        currentlyStreaming = false;
        logStreamCallback = new LogStreamCallback();
    }

    public Boolean isCurrentlyStreamed(String containerId) {
        // It is possible, that new LogStreamer was created, but log streaming
        // hasn't started yet, so we need to check if we're streaming right now
        return currentlyStreaming && this.containerId.equals(containerId);
    }

    public void startStreaming() {
        execute();
    }

    public void stopStreaming() {
        logStreamCallback.onComplete();
    }

    @Override
    protected Object doInBackground() {
        log.info("Starting log streaming for container {}", containerId);
        currentlyStreaming = true;
        try {
            dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)
                .withTailAll()
                .exec(logStreamCallback)
                .awaitCompletion();
        } catch (InterruptedException e) {
            log.warn("Interrupted while streaming logs for container {}", containerId);
        }
        log.info("Finished log streaming for container {}", containerId);
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String s : chunks) {
            logArea.append(s + "\n");
        }
    }

    private class LogStreamCallback extends ResultCallback.Adapter<Frame> {
        @Override
        public void onNext(Frame f) {
            publish(f.toString());
        }
    }
}
