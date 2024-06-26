package com.raunaqpahwa.codeexecutor.services;

import com.raunaqpahwa.codeexecutor.exceptions.CodeSizeLimitException;
import com.raunaqpahwa.codeexecutor.exceptions.ContainerNotCreatedException;
import com.raunaqpahwa.codeexecutor.exceptions.TimeLimitException;
import com.raunaqpahwa.codeexecutor.models.CodeResult;

public interface CodeExecutionServiceFactory {

    CodeResult executeCode(String language, String rawCode) throws CodeSizeLimitException,
            TimeLimitException, ContainerNotCreatedException;
}
