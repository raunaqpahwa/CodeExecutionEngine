package com.raunaqpahwa.codeexecutor.services;

import com.github.dockerjava.api.model.Container;

public interface ContainerRemovalService {

    void stopAndRemoveContainer(Container container);

    int getRemovalTasks();

    void incrementRemovalTasks();
}
