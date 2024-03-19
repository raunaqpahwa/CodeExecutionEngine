package com.raunaqpahwa.codeexecutor.controllers;

import com.raunaqpahwa.codeexecutor.models.Code;
import com.raunaqpahwa.codeexecutor.models.CodeResult;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.models.ImageInfo;
import com.raunaqpahwa.codeexecutor.models.LanguageInfo;
import com.raunaqpahwa.codeexecutor.services.CodeExecutionServiceFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RestController
class ExecutionController {

    private final CodeExecutionServiceFactory codeExecutionFactory;

    ExecutionController(CodeExecutionServiceFactory codeExecutionFactory) {
        this.codeExecutionFactory = codeExecutionFactory;
    }

    @PostMapping(value = "/python")
    public CodeResult executePythonCode(@RequestBody Code code) {
        return codeExecutionFactory.executeCode(Constants.EXECUTE_PYTHON, code.getRawCode());
    }

    @PostMapping(value = "/java")
    public CodeResult executeJavaCode(@RequestBody Code code) {
        return codeExecutionFactory.executeCode(Constants.EXECUTE_JAVA, code.getRawCode());
    }

    @PostMapping(value = "/javascript")
    public CodeResult executeJavascriptCode(@RequestBody Code code) {
        return codeExecutionFactory.executeCode(Constants.EXECUTE_JAVASCRIPT, code.getRawCode());
    }

    @GetMapping(value = "/info")
    public List<LanguageInfo> getSupportedLanguages() {
        return Arrays.stream(ImageInfo.values()).map(ImageInfo::getLanguageInfo)
                .sorted(Comparator.comparing(LanguageInfo::getLanguageName)).toList();
    }
}