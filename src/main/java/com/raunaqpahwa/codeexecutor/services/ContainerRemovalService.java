package com.raunaqpahwa.codeexecutor.services;

import com.raunaqpahwa.codeexecutor.models.DockerContainer;

import java.util.concurrent.CompletableFuture;

public interface ContainerRemovalService {

    CompletableFuture<Boolean> stopAndRemoveContainer(DockerContainer container);
}
