package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
import com.raunaqpahwa.codeexecutor.services.ContainerRemovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ContainerRemovalServiceImpl implements ContainerRemovalService {

    private static final Logger logger = LoggerFactory.getLogger(ContainerRemovalServiceImpl.class);

    private final DockerClient client;

    ContainerRemovalServiceImpl(DockerClient client) {
        this.client = client;
    }

    @Override
    @Async(value = "ContainerRemoval")
    public CompletableFuture<Boolean> stopAndRemoveContainer(DockerContainer container) {
        try {
            client.stopContainerCmd(container.getContainer().getId()).exec();
            client.removeContainerCmd(container.getContainer().getId()).exec();
            logger.info("Successfully removed the {} container with id {}", container.getLanguage().name(), container.getContainer().getId());
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            logger.error("An error {} occurred while removing {} container with id {}", e.getMessage(), container.getLanguage().name(),
                    container.getContainer().getId());
        }
        return CompletableFuture.completedFuture(false);
    }
}
