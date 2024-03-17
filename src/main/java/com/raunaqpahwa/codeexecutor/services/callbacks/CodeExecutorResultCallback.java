package com.raunaqpahwa.codeexecutor.services.callbacks;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.raunaqpahwa.codeexecutor.models.CodeResult;

public class CodeExecutorResultCallback extends ResultCallbackTemplate<CodeExecutorResultCallback, Frame> {

    private static final int startIndex = StreamType.STDOUT.name().length()+2;
    private final CodeResult.Builder codeResultBuilder;

    public CodeExecutorResultCallback(CodeResult.Builder codeResultBuilder) {
        this.codeResultBuilder = codeResultBuilder;
    }

    @Override
    public void onNext(Frame object) {
        if (StreamType.STDOUT.equals(object.getStreamType())) {
            codeResultBuilder.appendStdout(object.toString().substring(startIndex));
        } else if (StreamType.STDERR.equals(object.getStreamType())) {
            codeResultBuilder.appendStderr(object.toString().substring(startIndex));
        }
    }
}