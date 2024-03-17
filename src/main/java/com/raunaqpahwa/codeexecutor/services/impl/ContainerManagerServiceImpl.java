package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
import com.raunaqpahwa.codeexecutor.services.ContainerCreatorServiceFactory;
import com.raunaqpahwa.codeexecutor.services.ContainerManagerService;
import com.raunaqpahwa.codeexecutor.services.ContainerRemovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Service
public class ContainerManagerServiceImpl implements ContainerManagerService {

    private static final Logger logger = LoggerFactory.getLogger(ContainerManagerServiceImpl.class);

    private final DockerClient client;

    private final ContainerCreatorServiceFactory containerCreatorServiceFactory;

    private final ContainerRemovalService containerRemovalService;

    private final Queue<DockerContainer> containerQueue;

    ContainerManagerServiceImpl(ContainerCreatorServiceFactory containerCreatorServiceFactory, DockerClient client, Queue<DockerContainer> containerQueue, ContainerRemovalService containerRemovalService) {
        this.client = client;
        this.containerCreatorServiceFactory = containerCreatorServiceFactory;
        this.containerQueue = containerQueue;
        this.containerRemovalService = containerRemovalService;
    }

    @Override
    public DockerContainer createContainer(String language) {
        var container = containerCreatorServiceFactory.createContainer(language);
        if (container == null) {
            throw new IllegalStateException("Could not create docker container");
        }
        synchronized (containerQueue) {
            containerQueue.offer(container);
        }
        return container;
    }

    @Override
    public void startContainer(DockerContainer container) {
        var containerId = container.getContainer().getId();
        client.startContainerCmd(containerId).exec();
    }

    @Override
    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void stopAndRemoveContainers() {
        var currentTime = (double) System.currentTimeMillis() / 1000.0;
        while (!containerQueue.isEmpty()) {
            var container = containerQueue.peek();
            var timeDiffInSeconds = currentTime - container.getContainer().getCreated();
            if (timeDiffInSeconds >= 15) {
                containerQueue.poll();
                containerRemovalService.stopAndRemoveContainer(container).thenAccept(isRemoved -> {
                    if (isRemoved) {
                        containerCreatorServiceFactory.incrementAvailableContainers(container);
                    } else {
                        containerQueue.offer(container);
                    }
                });
            } else {
                break;
            }
        }
    }

    public void increaseRemovalPriority(DockerContainer container) {
        logger.info("Increasing priority of container {}", container.getContainer().getId());
        containerQueue.remove(container);
        container.setRemovalPriority(DockerContainer.RemovalPriority.HIGH);
        containerQueue.offer(container);
    }
}
