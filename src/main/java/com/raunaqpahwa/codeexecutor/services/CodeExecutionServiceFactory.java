package com.raunaqpahwa.codeexecutor.services;

import com.raunaqpahwa.codeexecutor.models.CodeResult;

public interface CodeExecutionServiceFactory {

    CodeResult executeCode(String language, String rawCode);
}
