package com.th30nlyw4y.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.lang3.NotImplementedException;

import java.time.Duration;

public class DockerConnection {
    private DockerClient client;
    private final String DEFAULT_LINUX_DOCKER_HOST = "unix:///var/run/docker.sock";
    private final String DEFAULT_WINDOWS_DOCKER_HOST = "npipe:////./pipe/docker_engine";

    public DockerConnection() {
        this(null);
    }

    public DockerConnection(String customDockerHostPath) {
        String socketPath;

        if (customDockerHostPath != null) {
            if (customDockerHostPath.startsWith("tcp")) {
                throw new NotImplementedException("TCP connection not implemented yet");
            } else {
                socketPath = customDockerHostPath;
            }
        } else {
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                socketPath = DEFAULT_WINDOWS_DOCKER_HOST;
            } else if (os.startsWith("Linux") || os.startsWith("Mac")) {
                socketPath = DEFAULT_LINUX_DOCKER_HOST;
            } else {
                throw new RuntimeException("Unknown os: " + os);
            }
        }

        DockerClientConfig dockerCfg = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(socketPath)
            .build();
        DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(dockerCfg.getDockerHost())
            .connectionTimeout(Duration.ZERO)
            .build();
        client = DockerClientImpl.getInstance(dockerCfg, dockerHttpClient);
    }

    public DockerClient getClient() {
        return client;
    }
}
