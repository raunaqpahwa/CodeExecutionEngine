package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
import com.raunaqpahwa.codeexecutor.services.CodeExecutionService;
import com.raunaqpahwa.codeexecutor.services.ContainerManagerService;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service(Constants.EXECUTE_PYTHON)
public class PythonCodeExecutionServiceImpl extends CodeExecutionService {

    private static final int TIMEOUT = 3;

    private static final String CONTAINER_TYPE = DockerContainer.Language.PYTHON.name();

    private static final Function<String, String> escapeFunc = Function.identity();

    PythonCodeExecutionServiceImpl(DockerClient client, ContainerManagerService containerManagerService) {
        super(client, containerManagerService);
    }

    @Override
    public String getContainerType() {
        return CONTAINER_TYPE;
    }

    @Override
    public int getTimeout() {
        return TIMEOUT;
    }

    @Override
    public Function<String, String> getEscapeFunc() {
        return escapeFunc;
    }

    @Override
    public ExecCreateCmdResponse createExecCmd(String containerId, String rawCode) {
        var createFileCmd = String.format("echo \"%s\" > %s", rawCode, Constants.PYTHON_FILE);
        var runFileCmd = String.format("python3 %s", Constants.PYTHON_FILE);
        var finalCmd = String.format("%s; %s", createFileCmd, runFileCmd);
        return client.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(Constants.BASH, Constants.LITERAL, finalCmd)
                .exec();
    }
}


