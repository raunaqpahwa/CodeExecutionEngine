package com.raunaqpahwa.codeexecutor.services;

import com.github.dockerjava.api.model.Container;

public interface ContainerCreatorServiceFactory {

    Container createContainer(String language);
}
