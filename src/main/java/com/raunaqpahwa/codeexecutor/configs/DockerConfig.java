package com.raunaqpahwa.codeexecutor.configs;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Bean
    public DockerClient createDockerClient() {
        var config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        var client = new ZerodepDockerHttpClient.Builder().dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig()).build();
        return DockerClientBuilder.getInstance(config).withDockerHttpClient(client).build();
    }
}
