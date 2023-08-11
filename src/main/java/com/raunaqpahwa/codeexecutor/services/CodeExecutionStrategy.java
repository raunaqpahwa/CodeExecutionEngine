package com.raunaqpahwa.codeexecutor.services;

import com.raunaqpahwa.codeexecutor.models.CodeResult;

public interface CodeExecutionStrategy {

    CodeResult runCode(String rawCode);

}
