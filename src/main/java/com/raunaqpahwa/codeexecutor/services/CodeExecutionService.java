package com.raunaqpahwa.codeexecutor.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.raunaqpahwa.codeexecutor.models.CodeResult;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.services.callbacks.CodeExecutorResultCallback;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class CodeExecutionService {

    protected final DockerClient client;

    protected final ContainerManagerService containerManagerService;

    public CodeExecutionService(DockerClient client, ContainerManagerService containerManagerService) {
        this.client = client;
        this.containerManagerService = containerManagerService;
    }

    abstract public String getContainerType();

    abstract public int getTimeout();

    abstract public ExecCreateCmdResponse createExecCmd(String containerId, String rawCode);

    abstract public Function<String, String> getEscapeFunc();

    private double codeSizeMb(String rawCode) {
        var codeBytes = rawCode.getBytes();
        return ((double) codeBytes.length / (1024 * 1024));
    }

    public CodeResult executeCode(String rawCode) {
        var container = containerManagerService.createContainer(getContainerType());
        containerManagerService.startContainer(container);
        var codeSize = codeSizeMb(rawCode);
        var escapeFunc = getEscapeFunc();
        if (codeSize > Constants.CODE_SIZE_LIMIT) {
            // TODO: Throw exception if code size beyond limit
        }
        return executeCodeInContainer(container.getId(), escapeFunc.apply(rawCode));
    }

    public CodeResult executeCodeInContainer(String containerId, String rawCode) {
        var codeResultBuilder = new CodeResult.Builder();
        var resultCallbackTemplate = new CodeExecutorResultCallback(codeResultBuilder);
        var execCreateCmdResponse = createExecCmd(containerId, rawCode);
        var timeout = getTimeout();

        boolean result = false;
        try {
            result = client.execStartCmd(execCreateCmdResponse.getId()).exec(resultCallbackTemplate)
                    .awaitCompletion(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            codeResultBuilder.appendExceptions(e.toString());
        }

        if (!result) {
            codeResultBuilder.clear().appendExceptions(String.format("Your code failed to execute in the permitted time of %d seconds", timeout));
        }

        return codeResultBuilder.build();
    }
}
