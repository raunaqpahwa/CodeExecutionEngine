package com.raunaqpahwa.codeexecutor.services;

import com.github.dockerjava.api.model.Container;

import java.util.concurrent.CompletableFuture;

public interface ContainerRemovalService {

    CompletableFuture<Boolean> stopAndRemoveContainer(Container container);
}
