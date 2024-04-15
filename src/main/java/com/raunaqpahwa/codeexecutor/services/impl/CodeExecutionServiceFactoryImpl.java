package com.raunaqpahwa.codeexecutor.services.impl;

import com.raunaqpahwa.codeexecutor.exceptions.CodeSizeLimitException;
import com.raunaqpahwa.codeexecutor.exceptions.ContainerNotCreatedException;
import com.raunaqpahwa.codeexecutor.exceptions.TimeLimitException;
import com.raunaqpahwa.codeexecutor.models.CodeResult;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.services.CodeExecutionService;
import com.raunaqpahwa.codeexecutor.services.CodeExecutionServiceFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CodeExecutionServiceFactoryImpl implements CodeExecutionServiceFactory {

    private final Map<String, CodeExecutionService> codeExecutionServiceMap;

    CodeExecutionServiceFactoryImpl(Map<String, CodeExecutionService> codeExecutionServiceMap) {
        this.codeExecutionServiceMap = codeExecutionServiceMap;
    }

    private CodeExecutionService getCodeExecutionType(String language) {
        var codeExecutionService = codeExecutionServiceMap.get(language);
        if (codeExecutionService == null) {
            throw new UnsupportedOperationException(Constants.LANGUAGE_UNSUPPORTED);
        }
        return codeExecutionService;
    }

    @Override
    public CodeResult executeCode(String language, String rawCode) throws CodeSizeLimitException,
            TimeLimitException, ContainerNotCreatedException {
        var codeExecutionService = getCodeExecutionType(language);
        return codeExecutionService.executeCode(rawCode);
    }
}
