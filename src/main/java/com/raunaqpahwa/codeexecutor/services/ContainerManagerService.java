package com.raunaqpahwa.codeexecutor.services;

import com.github.dockerjava.api.model.Container;

public interface ContainerManagerService {

    Container createContainer(String language);

    void startContainer(Container container);

    void stopAndRemoveContainers();
}
