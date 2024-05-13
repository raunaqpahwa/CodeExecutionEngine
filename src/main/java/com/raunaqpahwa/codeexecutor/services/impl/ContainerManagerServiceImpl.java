package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.raunaqpahwa.codeexecutor.exceptions.ContainerNotCreatedException;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
import com.raunaqpahwa.codeexecutor.services.ContainerCreatorServiceFactory;
import com.raunaqpahwa.codeexecutor.services.ContainerManagerService;
import com.raunaqpahwa.codeexecutor.services.ContainerRemovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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
    public DockerContainer createContainer(String language) throws ContainerNotCreatedException {
        var container = containerCreatorServiceFactory.createContainer(language);
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
        var containersToRemove = new ArrayList<DockerContainer>();
        synchronized (containerQueue) {
            int numContainersToRemove = Math.min(containerQueue.size(), 3);
            for (int i = 0; i < numContainersToRemove; i++) {
                var container = containerQueue.peek();
                var timeDiffInSeconds = currentTime - container.getContainer().getCreated();
                if (timeDiffInSeconds >= 10) {
                    containersToRemove.add(containerQueue.poll());
                } else {
                    break;
                }
            }
        }
        for (var container: containersToRemove) {
            containerRemovalService.stopAndRemoveContainer(container.getContainer()).thenAccept(isRemoved -> {
                if (isRemoved) {
                    containerCreatorServiceFactory.incrementAvailableContainers(container);
                } else {
                    synchronized (containerQueue) {
                        containerQueue.offer(container);
                    }
                }
            });
        }
    }

    @Override
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void stopAndRemoveGhostContainers() {
        var currentTime = (double) System.currentTimeMillis() / 1000.0;
        var containers = client.listContainersCmd().exec();
        containers.sort(Comparator.comparing(Container::getCreated));
        for (var container: containers) {
            var timeDiffInMinutes = (currentTime - container.getCreated()) / 60.0;
            if (timeDiffInMinutes >= 10) {
                containerRemovalService.stopAndRemoveContainer(container);
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
