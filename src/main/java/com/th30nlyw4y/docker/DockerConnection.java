package com.th30nlyw4y.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.time.Duration;

public class DockerConnection {
    private final DockerClient client;

    public DockerConnection() {
        this(null);
    }

    public DockerConnection(String customDockerHostPath) {
        DockerClientConfig dockerCfg = customDockerHostPath != null ?
            DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(customDockerHostPath).build() :
            DefaultDockerClientConfig.createDefaultConfigBuilder().build();
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
