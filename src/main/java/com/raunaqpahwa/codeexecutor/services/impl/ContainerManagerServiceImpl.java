package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
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

    private final Queue<Container> containerQueue;

    ContainerManagerServiceImpl(ContainerCreatorServiceFactory containerCreatorServiceFactory, DockerClient client,
                                Queue<Container> containerQueue, ContainerRemovalService containerRemovalService) {
        this.client = client;
        this.containerCreatorServiceFactory = containerCreatorServiceFactory;
        this.containerQueue = containerQueue;
        this.containerRemovalService = containerRemovalService;
    }

    @Override
    public Container createContainer(String language) {
        var container = containerCreatorServiceFactory.createContainer(language);
        synchronized (containerQueue) {
            containerQueue.offer(container);
        }
        return container;
    }

    @Override
    public void startContainer(Container container) {
        client.startContainerCmd(container.getId()).exec();
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void stopAndRemoveContainers() {
        long currentTime = System.currentTimeMillis() / 1000;
        logger.info("Current removal tasks {}", containerRemovalService.getRemovalTasks());
        while (!containerQueue.isEmpty()) {
            var container = containerQueue.peek();
            double timeDiffInSeconds = currentTime - container.getCreated();
            if (timeDiffInSeconds >= 10 && containerRemovalService.getRemovalTasks() < 100) {
                containerRemovalService.incrementRemovalTasks();
                containerQueue.poll();
                containerRemovalService.stopAndRemoveContainer(container);
            } else {
                break;
            }
        }
    }
}
