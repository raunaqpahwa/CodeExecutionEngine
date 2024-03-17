package com.raunaqpahwa.codeexecutor.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.HostConfig;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ContainerCreatorService {

    protected final DockerClient client;

    private final AtomicInteger availableContainers;

    public ContainerCreatorService(DockerClient client, int maxContainers) {
        this.client = client;
        this.availableContainers = new AtomicInteger(maxContainers);
    }

    public DockerContainer createContainer() {
        int currentValue = getAvailableContainers();
        int newValue =  currentValue - 1;
        if (currentValue > 0 && availableContainers.compareAndSet(currentValue, newValue)) {
            var hostConfig = HostConfig.newHostConfig().withMemory(getMaxMemory());
            CreateContainerCmd containerCmd = client.createContainerCmd(getImageName())
                    .withNetworkDisabled(true)
                    .withTty(true)
                    .withWorkingDir(getWorkDir())
                    .withHostConfig(hostConfig);

            var createContainerResponse = containerCmd.exec();
            // TODO: Add container not created error in throw
            return Optional.ofNullable(createContainerResponse)
                    .map(v -> {
                        var container = client.listContainersCmd().withShowAll(true).withIdFilter(List.of(v.getId()))
                                .exec().stream().findFirst().orElseThrow();
                        return new DockerContainer(container, getLanguage(), DockerContainer.RemovalPriority.LOW);
                    })
                    .orElseThrow();
        }
        return null;
    }

    public int getAvailableContainers() {
        return availableContainers.get();
    }

    public void incrementAvailableContainers() {
        int oldValue = getAvailableContainers();
        int newValue = oldValue + 1;
        availableContainers.compareAndSet(oldValue, newValue);
    }

    abstract public String getImageName();

    abstract public String getWorkDir();

    abstract public long getMaxMemory();

    abstract public DockerContainer.Language getLanguage();
}
