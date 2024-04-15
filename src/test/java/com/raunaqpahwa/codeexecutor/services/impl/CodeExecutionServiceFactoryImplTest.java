package com.raunaqpahwa.codeexecutor.services.impl;

import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.services.CodeExecutionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CodeExecutionServiceFactoryImplTest {

    @InjectMocks
    CodeExecutionServiceFactoryImpl codeExecutionServiceFactory;

    @Mock
    private Map<String, CodeExecutionService> codeExecutionServiceMap;

    @Test
    @DisplayName("Happy path: Code is executed")
    public void testExecuteCode() throws Exception {
        var codeExecutionService = mock(CodeExecutionService.class);
        String rawCode = "print('Hello World')";
        when(codeExecutionServiceMap.get(Constants.EXECUTE_PYTHON)).thenReturn(codeExecutionService);

        codeExecutionServiceFactory.executeCode(Constants.EXECUTE_PYTHON, rawCode);
        verify(codeExecutionServiceMap).get(Constants.EXECUTE_PYTHON);
        verify(codeExecutionService).executeCode(rawCode);
    }

    @Test
    @DisplayName("Error path: Language unsupported")
    public void testExecuteCodeLanguageUnsupported() {
        String language = "Golang";
        String rawCode = """
                    package main
                    import "fmt"
                    func main() {
                        fmt.Println("Hello World")
                    }
                """;
        when(codeExecutionServiceMap.get(language)).thenReturn(null);
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> codeExecutionServiceFactory.executeCode(language, rawCode),
                Constants.LANGUAGE_UNSUPPORTED
                );
    }
}
