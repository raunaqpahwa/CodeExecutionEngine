package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.models.ImageInfo;
import com.raunaqpahwa.codeexecutor.services.ContainerCreatorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service(Constants.CREATE_PYTHON)
public class PythonContainerCreatorServiceImpl implements ContainerCreatorService {

    private static final long MAX_MEMORY = 100 * 1024 * 1024;

    private final DockerClient client;

    PythonContainerCreatorServiceImpl(DockerClient client) {
        this.client = client;
    }
    @Override
    public Container createContainer() {
        var hostConfig = HostConfig.newHostConfig().withMemory(MAX_MEMORY);
        CreateContainerCmd containerCmd = client.createContainerCmd(ImageInfo.PYTHON.getNameWithTag())
                .withNetworkDisabled(true)
                .withTty(true)
                .withWorkingDir(Constants.PYTHON_WORK_DIR)
                .withHostConfig(hostConfig);
        var createContainerResponse = containerCmd.exec();
        // TODO: Add container not created error in throw
        return Optional.ofNullable(createContainerResponse)
                .map(v -> client.listContainersCmd().withShowAll(true).withIdFilter(List.of(v.getId()))
                        .exec().stream().findFirst().orElseThrow())
                .orElseThrow();
    }
}
