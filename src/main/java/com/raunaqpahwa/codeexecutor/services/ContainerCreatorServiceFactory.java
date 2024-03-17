package com.raunaqpahwa.codeexecutor.services;

import com.raunaqpahwa.codeexecutor.models.DockerContainer;

public interface ContainerCreatorServiceFactory {

    DockerContainer createContainer(String language);

    void incrementAvailableContainers(DockerContainer container);
}
