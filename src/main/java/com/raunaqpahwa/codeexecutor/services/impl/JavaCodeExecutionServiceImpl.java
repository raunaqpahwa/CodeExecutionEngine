package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
import com.raunaqpahwa.codeexecutor.services.CodeExecutionService;
import com.raunaqpahwa.codeexecutor.services.ContainerManagerService;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service(Constants.EXECUTE_JAVA)
public class JavaCodeExecutionServiceImpl extends CodeExecutionService {

    private static final int TIMEOUT = 3;

    private static final String CONTAINER_TYPE = DockerContainer.Language.JAVA.name();

    private static final Function<String, String> escapeFunc = StringEscapeUtils::escapeJava;

    JavaCodeExecutionServiceImpl(DockerClient client, ContainerManagerService containerManagerService) {
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
        var createFileCmd = String.format("echo \"%s\" > %s", rawCode, Constants.JAVA_FILE);
        var compileFileCmd = String.format("javac %s", Constants.JAVA_FILE);
        var runFileCmd = String.format("java %s", Constants.JAVA_FILE_COMPILED);
        var finalCmd = String.format("%s; %s; %s", createFileCmd, compileFileCmd, runFileCmd);
        return client.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withWorkingDir(Constants.JAVA_WORK_DIR)
                .withCmd(Constants.BASH, Constants.LITERAL, finalCmd)
                .exec();
    }
}

