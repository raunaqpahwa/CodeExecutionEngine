package com.raunaqpahwa.codeexecutor.services.impl;

import com.raunaqpahwa.codeexecutor.exceptions.ContainerNotCreatedException;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
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
            throw new UnsupportedOperationException(Constants.LANGUAGE_UNSUPPORTED);
        }
        return containerCreatorService;
    }

    @Override
    public DockerContainer createContainer(String language) throws ContainerNotCreatedException {
        var containerCreatorService = getContainerCreatorService(language);
        return containerCreatorService.createContainer();
    }

    @Override
    public void incrementAvailableContainers(DockerContainer container) {
        var containerCreatorService = getContainerCreatorService(container.getLanguage().name());
        containerCreatorService.incrementAvailableContainers();
    }
}
