package com.raunaqpahwa.codeexecutor.services;

import com.raunaqpahwa.codeexecutor.exceptions.ContainerNotCreatedException;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;

public interface ContainerManagerService {

    DockerContainer createContainer(String language) throws ContainerNotCreatedException;

    void startContainer(DockerContainer container);

    void stopAndRemoveContainers();

    void stopAndRemoveGhostContainers();

    void increaseRemovalPriority(DockerContainer container);
}
