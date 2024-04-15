package com.raunaqpahwa.codeexecutor.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raunaqpahwa.codeexecutor.exceptions.CodeSizeLimitException;
import com.raunaqpahwa.codeexecutor.exceptions.ContainerNotCreatedException;
import com.raunaqpahwa.codeexecutor.exceptions.TimeLimitException;
import com.raunaqpahwa.codeexecutor.models.Code;
import com.raunaqpahwa.codeexecutor.models.CodeResult;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.services.CodeExecutionServiceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@WebMvcTest(ExecutionController.class)
@AutoConfigureMockMvc
public class ExecutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CodeExecutionServiceFactory codeExecutionServiceFactory;

    @Test
    @DisplayName("Happy path: Java Hello World")
    public void testExecuteJavaCodeHelloWorld() throws Exception {
        String javaCode = """
                class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello World");
                    }
                }
                """;
        var codeResult = new CodeResult.Builder().appendStdout("Hello World").build();
        var code = new Code();
        code.setRawCode(javaCode);

        when(codeExecutionServiceFactory.executeCode(Constants.EXECUTE_JAVA, code.getRawCode())).thenReturn(codeResult);
        String jsonCode = null;
        try {
            jsonCode = objectMapper.writeValueAsString(code);
        } catch (JsonProcessingException e) {
            fail("Failed to parse JSON for code request");
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/java")
                        .content(jsonCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stdout").value("Hello World"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stderr").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exceptions").value(""));
    }

    @Test
    @DisplayName("Error path: Java time limit exceeded")
    public void testExecuteJavaCodeTimeLimit() throws Exception {
        String javaCode = """
                class Main {
                    public static void main(String[] args) {
                        while (true) {
                            System.out.println("Hello World");
                        }
                    }
                }
        """;
        var code = new Code();
        code.setRawCode(javaCode);

        when(codeExecutionServiceFactory.executeCode(Constants.EXECUTE_JAVA, code.getRawCode()))
                .thenThrow(new TimeLimitException("Your code failed to execute in the permitted time of 3 seconds."));
        String jsonCode = null;
        try {
            jsonCode = objectMapper.writeValueAsString(code);
        } catch (JsonProcessingException e) {
            fail("Failed to parse JSON for code request");
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/java")
                        .content(jsonCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stdout").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stderr").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exceptions").value("Your code failed to execute in the permitted time of 3 seconds."));
    }

    @Test
    @DisplayName("Error path: Java code size limit")
    public void testExecuteJavaCodeSizeLimit() throws Exception {
        String javaCode = """
                class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello World");
                    }
                }
        """;
        var code = new Code();
        code.setRawCode(javaCode);

        when(codeExecutionServiceFactory.executeCode(Constants.EXECUTE_JAVA, code.getRawCode()))
                .thenThrow(new CodeSizeLimitException(Constants.CODE_SIZE_LIMIT_EXCEPTION));
        String jsonCode = null;
        try {
            jsonCode = objectMapper.writeValueAsString(code);
        } catch (JsonProcessingException e) {
            fail("Failed to parse JSON for code request");
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/java")
                        .content(jsonCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stdout").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stderr").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exceptions").value(Constants.CODE_SIZE_LIMIT_EXCEPTION));

    }

    @Test
    @DisplayName("Error path: Java container not created")
    public void testExecuteJavaCodeContainerNotCreated() throws Exception {
        String javaCode = """
                class Main {
                    public static void main(String[] args) {
                        System.out.println("Hello World");
                    }
                }
        """;
        var code = new Code();
        code.setRawCode(javaCode);

        when(codeExecutionServiceFactory.executeCode(Constants.EXECUTE_JAVA, code.getRawCode()))
                .thenThrow(new ContainerNotCreatedException(Constants.CONTAINER_NOT_CREATED_EXCEPTION));
        String jsonCode = null;
        try {
            jsonCode = objectMapper.writeValueAsString(code);
        } catch (JsonProcessingException e) {
            fail("Failed to parse JSON for code request");
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/java")
                        .content(jsonCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stdout").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stderr").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exceptions").value(Constants.CONTAINER_NOT_CREATED_EXCEPTION));
    }

    @Test
    @DisplayName("Happy path: Python Hello World")
    public void testExecutePythonCodeHelloWorld() throws Exception {
        String pythonCode = "print('Hello World')";
        var codeResult = new CodeResult.Builder().appendStdout("Hello World").build();
        var code = new Code();
        code.setRawCode(pythonCode);

        when(codeExecutionServiceFactory.executeCode(Constants.EXECUTE_PYTHON, code.getRawCode())).thenReturn(codeResult);
        String jsonCode = null;
        try {
            jsonCode = objectMapper.writeValueAsString(code);
        } catch (JsonProcessingException e) {
            fail("Failed to parse JSON for code request");
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/python")
                        .content(jsonCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stdout").value("Hello World"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stderr").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exceptions").value(""));
    }

    @Test
    @DisplayName("Error path: Python time limit exceeded")
    public void testExecutePythonCodeTimeLimit() throws Exception {
        String pythonCode = """
                while True:
                    print('Hello World')
        """;
        var code = new Code();
        code.setRawCode(pythonCode);

        when(codeExecutionServiceFactory.executeCode(Constants.EXECUTE_PYTHON, code.getRawCode()))
                .thenThrow(new TimeLimitException("Your code failed to execute in the permitted time of 3 seconds."));
        String jsonCode = null;
        try {
            jsonCode = objectMapper.writeValueAsString(code);
        } catch (JsonProcessingException e) {
            fail("Failed to parse JSON for code request");
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/python")
                        .content(jsonCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stdout").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stderr").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exceptions").value("Your code failed to execute in the permitted time of 3 seconds."));
    }

    @Test
    @DisplayName("Happy path: Javascript Hello World")
    public void testExecuteJavascriptCodeHelloWorld() throws Exception {
        String javascriptCode = "console.log('Hello World')";
        var codeResult = new CodeResult.Builder().appendStdout("Hello World").build();
        var code = new Code();
        code.setRawCode(javascriptCode);

        when(codeExecutionServiceFactory.executeCode(Constants.EXECUTE_JAVASCRIPT, code.getRawCode())).thenReturn(codeResult);
        String jsonCode = null;
        try {
            jsonCode = objectMapper.writeValueAsString(code);
        } catch (JsonProcessingException e) {
            fail("Failed to parse JSON for code request");
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/javascript")
                        .content(jsonCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stdout").value("Hello World"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stderr").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exceptions").value(""));
    }

    @Test
    @DisplayName("Error path: Javascript time limit exceeded")
    public void testExecuteJavascriptCodeTimeLimit() throws Exception {
        String javascriptCode = """
                while (true) {
                    console.log('Hello World')
                }
        """;
        var code = new Code();
        code.setRawCode(javascriptCode);

        when(codeExecutionServiceFactory.executeCode(Constants.EXECUTE_JAVASCRIPT, code.getRawCode()))
                .thenThrow(new TimeLimitException("Your code failed to execute in the permitted time of 3 seconds."));
        String jsonCode = null;
        try {
            jsonCode = objectMapper.writeValueAsString(code);
        } catch (JsonProcessingException e) {
            fail("Failed to parse JSON for code request");
        }

        mockMvc.perform(MockMvcRequestBuilders.post("/javascript")
                        .content(jsonCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.stdout").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stderr").value(""))
                .andExpect(MockMvcResultMatchers.jsonPath("$.exceptions").value("Your code failed to execute in the permitted time of 3 seconds."));
    }
}