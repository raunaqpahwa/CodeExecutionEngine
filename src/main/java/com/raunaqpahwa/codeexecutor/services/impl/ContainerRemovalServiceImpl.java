package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.raunaqpahwa.codeexecutor.services.ContainerRemovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ContainerRemovalServiceImpl implements ContainerRemovalService {

    private static final Logger logger = LoggerFactory.getLogger(ContainerRemovalServiceImpl.class);

    private final DockerClient client;

    private final Queue<Container> containerQueue;

    private final AtomicInteger removalTasks;

    ContainerRemovalServiceImpl(DockerClient client, Queue<Container> containerQueue) {
        this.client = client;
        this.containerQueue = containerQueue;
        this.removalTasks = new AtomicInteger(0);
    }

    @Override
    @Async(value = "ContainerRemoval")
    public void stopAndRemoveContainer(Container container) {
        try {
            client.stopContainerCmd(container.getId()).exec();
            client.removeContainerCmd(container.getId()).exec();
        } catch (Exception e) {
            logger.error("An error occurred while removing container {}", container.getId());
            synchronized (containerQueue) {
                containerQueue.offer(container);
            }
        } finally {
            decrementRemovalTasks();
        }
    }

    @Override
    public int getRemovalTasks() {
        return removalTasks.get();
    }

    @Override
    public void incrementRemovalTasks() {
        removalTasks.incrementAndGet();
    }

    private void decrementRemovalTasks() {
        removalTasks.decrementAndGet();
    }
}
