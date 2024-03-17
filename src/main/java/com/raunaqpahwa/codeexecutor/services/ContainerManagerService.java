package com.raunaqpahwa.codeexecutor.services;

import com.raunaqpahwa.codeexecutor.models.DockerContainer;

public interface ContainerManagerService {

    DockerContainer createContainer(String language);

    void startContainer(DockerContainer container);

    void stopAndRemoveContainers();

    void increaseRemovalPriority(DockerContainer container);
}
