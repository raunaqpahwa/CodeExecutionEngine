package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.raunaqpahwa.codeexecutor.services.ContainerRemovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@EnableAsync
public class ContainerRemovalServiceImpl implements ContainerRemovalService {

    private static final Logger logger = LoggerFactory.getLogger(ContainerRemovalServiceImpl.class);

    private final DockerClient client;

    ContainerRemovalServiceImpl(DockerClient client) {
        this.client = client;
    }

    @Override
    @Async(value = "ContainerRemoval")
    public CompletableFuture<Boolean> stopAndRemoveContainer(Container container) {
        try {
            client.stopContainerCmd(container.getId()).exec();
            client.removeContainerCmd(container.getId()).exec();
            logger.info("Successfully removed the container with id {}", container.getId());
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            logger.error("An error {} occurred while removing container id {}", e.getMessage(),
                    container.getId());
        }
        return CompletableFuture.completedFuture(false);
    }
}
