package com.raunaqpahwa.codeexecutor.utils;

import com.github.dockerjava.api.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TerminateContainerUtil {

    @Autowired
    DockerClient client;

    @Async
    public void terminateContainer(String containerID) {
        client.stopContainerCmd(containerID).exec();
        client.removeContainerCmd(containerID).exec();
    }
}
