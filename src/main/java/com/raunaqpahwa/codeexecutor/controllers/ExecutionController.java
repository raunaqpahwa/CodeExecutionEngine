package com.raunaqpahwa.codeexecutor.controllers;

import com.raunaqpahwa.codeexecutor.models.Code;
import com.raunaqpahwa.codeexecutor.models.CodeResult;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.services.impl.CodeExecutionServiceFactoryImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(method = RequestMethod.POST)
class ExecutionController {

    private final CodeExecutionServiceFactoryImpl codeExecutionFactory;

    ExecutionController(CodeExecutionServiceFactoryImpl codeExecutionFactory) {
        this.codeExecutionFactory = codeExecutionFactory;
    }

    @RequestMapping(value = "/python")
    public CodeResult executePythonCode(@RequestBody Code code) {
        return codeExecutionFactory.executeCode(Constants.EXECUTE_PYTHON, code.getRawCode());
    }

    @RequestMapping(value = "/java")
    public CodeResult executeJavaCode(@RequestBody Code code) {
        return codeExecutionFactory.executeCode(Constants.EXECUTE_JAVA, code.getRawCode());
    }

    @RequestMapping(value = "/javascript")
    public CodeResult executeJavascriptCode(@RequestBody Code code) {
        return codeExecutionFactory.executeCode(Constants.EXECUTE_JAVASCRIPT, code.getRawCode());
    }
}