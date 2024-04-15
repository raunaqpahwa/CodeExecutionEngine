package com.raunaqpahwa.codeexecutor.services;

import com.raunaqpahwa.codeexecutor.exceptions.ContainerNotCreatedException;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;

public interface ContainerCreatorServiceFactory {

    DockerContainer createContainer(String language) throws ContainerNotCreatedException;

    void incrementAvailableContainers(DockerContainer container);
}
