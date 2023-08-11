package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.raunaqpahwa.codeexecutor.models.CodeResult;
import com.raunaqpahwa.codeexecutor.services.CodeExecutionStrategy;
import com.raunaqpahwa.codeexecutor.services.callbacks.TimeoutResultCallback;
import com.raunaqpahwa.codeexecutor.utils.TerminateContainerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.*;

@Component
@Qualifier("PythonCodeExecutionStrategy")
public class PythonCodeExecutionStrategy implements CodeExecutionStrategy {

    public static final int TIMEOUT = 3;

    @Autowired
    private DockerClient client;

    @Autowired
    private TerminateContainerUtil terminateContainerUtil;

    @Override
    public CodeResult runCode(String rawCode) {
        CreateContainerResponse container = createContainer();
        CodeResult codeResult = runPythonCode(container.getId(), rawCode);
        terminateContainerUtil.terminateContainer(container.getId());
        return codeResult;
    }

    private CreateContainerResponse createContainer() {
        long memoryLimit = 100 * 1024 * 1024;
        HostConfig hostConfig = HostConfig.newHostConfig().withMemory(memoryLimit);
        CreateContainerCmd containerCmd = client.createContainerCmd("python:latest")
                .withName("python3_" + UUID.randomUUID())
                .withNetworkDisabled(true)
                .withTty(true)
                .withHostConfig(hostConfig);
        return containerCmd.exec();
    }

    private CodeResult runPythonCode(String containerID, String rawCode) {
        CodeResult codeResult = new CodeResult();
        StringBuilder standardOutputLogs = new StringBuilder();
        StringBuilder standardErrorLogs = new StringBuilder();
        StringBuilder exceptions = new StringBuilder();
        ResultCallbackTemplate resultCallbackTemplate = new TimeoutResultCallback(standardOutputLogs, standardErrorLogs, exceptions);

        client.startContainerCmd(containerID).exec();

        ExecCreateCmdResponse execCreateCmdResponse = createCodeExecCmd(containerID, rawCode);

        boolean result = false;
        try {
            result = client.execStartCmd(execCreateCmdResponse.getId()).exec(resultCallbackTemplate)
                    .awaitCompletion(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            exceptions.append(e + "\n");
        }

        if (!result) {
            exceptions.append(String.format("Your code failed to execute in the allowed time of %d seconds", TIMEOUT));
            standardOutputLogs.setLength(0);
            standardErrorLogs.setLength(0);
        }
        codeResult.setStandardOutput(standardOutputLogs.toString());
        codeResult.setStandardError(standardErrorLogs.toString());
        codeResult.setExceptions(exceptions.toString());

        return codeResult;
    }

    private ExecCreateCmdResponse createCodeExecCmd(String containerID, String rawCode) {
        return client.execCreateCmd(containerID)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd("python3", "-c", rawCode)
                .exec();
    }
}


