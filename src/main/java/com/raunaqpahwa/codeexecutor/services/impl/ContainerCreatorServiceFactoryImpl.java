package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.model.Container;
import com.raunaqpahwa.codeexecutor.services.ContainerCreatorService;
import com.raunaqpahwa.codeexecutor.services.ContainerCreatorServiceFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ContainerCreatorServiceFactoryImpl implements ContainerCreatorServiceFactory {

    private final Map<String, ContainerCreatorService> containerCreatorServiceMap;

    ContainerCreatorServiceFactoryImpl(Map<String, ContainerCreatorService> containerCreatorServiceMap) {
        this.containerCreatorServiceMap = containerCreatorServiceMap;
    }

    private ContainerCreatorService getContainerCreatorService(String language) {
        var containerCreatorService = containerCreatorServiceMap.get(language);
        if (containerCreatorService == null) {
            throw new UnsupportedOperationException("This language is not supported");
        }
        return containerCreatorService;
    }

    @Override
    public Container createContainer(String language) {
        var containerCreatorService = getContainerCreatorService(language);
        return containerCreatorService.createContainer();
    }
}
