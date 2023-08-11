package com.raunaqpahwa.codeexecutor.controllers;

import com.raunaqpahwa.codeexecutor.models.Code;
import com.raunaqpahwa.codeexecutor.models.CodeResult;
import com.raunaqpahwa.codeexecutor.services.CodeExecutionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(method = RequestMethod.POST)
class ExecutionController {

    @Autowired
    @Qualifier("PythonCodeExecutionStrategy")
    CodeExecutionStrategy pythonCodeExecutionStrategy;

    @Autowired
    @Qualifier("JavaCodeExecutionStrategy")
    CodeExecutionStrategy javaCodeExecutionStrategy;

    @Autowired
    @Qualifier("JavascriptCodeExecutionStrategy")
    CodeExecutionStrategy javascriptCodeExecutionStrategy;

    @RequestMapping(method = RequestMethod.POST, value = "/python")
    public CodeResult runPythonCode(@RequestBody Code code) {
        return pythonCodeExecutionStrategy.runCode(code.getRawCode());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/java")
    public CodeResult runJavaCode(@RequestBody Code code) {
        return javaCodeExecutionStrategy.runCode(code.getRawCode());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/javascript")
    public CodeResult runJavascriptCode(@RequestBody Code code) {
        return javascriptCodeExecutionStrategy.runCode(code.getRawCode());
    }
}