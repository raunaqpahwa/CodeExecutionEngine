package com.raunaqpahwa.codeexecutor.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.raunaqpahwa.codeexecutor.exceptions.CodeSizeLimitException;
import com.raunaqpahwa.codeexecutor.exceptions.ContainerNotCreatedException;
import com.raunaqpahwa.codeexecutor.exceptions.TimeLimitException;
import com.raunaqpahwa.codeexecutor.models.CodeResult;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
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

    public CodeResult executeCode(String rawCode) throws CodeSizeLimitException,
            TimeLimitException, ContainerNotCreatedException {
        var container = containerManagerService.createContainer(getContainerType());
        containerManagerService.startContainer(container);
        var codeSize = codeSizeMb(rawCode);
        var escapeFunc = getEscapeFunc();
        if (codeSize > Constants.CODE_SIZE_LIMIT) {
            throw new CodeSizeLimitException(Constants.CODE_SIZE_LIMIT_EXCEPTION);
        }
        return executeCodeInContainer(container, escapeFunc.apply(rawCode));
    }

    private CodeResult executeCodeInContainer(DockerContainer container, String rawCode) throws TimeLimitException {
        var codeResultBuilder = new CodeResult.Builder();
        var resultCallbackTemplate = new CodeExecutorResultCallback(codeResultBuilder);
        var execCreateCmdResponse = createExecCmd(container.getContainer().getId(), rawCode);
        var timeout = getTimeout();

        var result = false;
        try {
            result = client.execStartCmd(execCreateCmdResponse.getId()).exec(resultCallbackTemplate)
                    .awaitCompletion(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            codeResultBuilder.appendExceptions(e.getMessage());
        }

        if (!result) {
            containerManagerService.increaseRemovalPriority(container);
            throw new TimeLimitException(String.format(Constants.TIME_LIMIT_EXCEPTION, timeout));
        }

        return codeResultBuilder.build();
    }
}
