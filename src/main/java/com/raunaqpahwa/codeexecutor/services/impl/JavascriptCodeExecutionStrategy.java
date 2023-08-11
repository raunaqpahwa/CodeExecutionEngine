package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.raunaqpahwa.codeexecutor.models.CodeResult;
import com.raunaqpahwa.codeexecutor.services.CodeExecutionStrategy;
import com.raunaqpahwa.codeexecutor.services.callbacks.TimeoutResultCallback;
import com.raunaqpahwa.codeexecutor.utils.TerminateContainerUtil;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Qualifier("JavascriptCodeExecutionStrategy")
public class JavascriptCodeExecutionStrategy implements CodeExecutionStrategy {

    public static final int TIMEOUT = 3;

    @Autowired
    private DockerClient client;

    @Autowired
    private TerminateContainerUtil terminateContainerUtil;

    @Override
    public CodeResult runCode(String rawCode) {
        CreateContainerResponse container = createContainer();
        CodeResult codeResult = runJavascriptCode(container.getId(), rawCode);
        terminateContainerUtil.terminateContainer(container.getId());
        return codeResult;
    }

    private CodeResult runJavascriptCode(String containerID, String rawCode) {
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

    private CreateContainerResponse createContainer() {
        long memoryLimit = 100 * 1024 * 1024;
        HostConfig hostConfig = HostConfig.newHostConfig().withMemory(memoryLimit).withPrivileged(true);
        CreateContainerCmd containerCmd = client.createContainerCmd("node:latest")
                .withName("node_" + UUID.randomUUID())
                .withTty(true)
                .withNetworkDisabled(true)
                .withWorkingDir("/")
                .withHostConfig(hostConfig);
        return containerCmd.exec();
    }

    private ExecCreateCmdResponse createCodeExecCmd(String containerID, String rawCode) {
        String writeFileRunCommand = String.format("echo \"%s\" > index.js; node index.js", StringEscapeUtils.escapeJava(rawCode));
        return client.execCreateCmd(containerID)
                .withCmd("bash", "-c", writeFileRunCommand)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();
    }
}
