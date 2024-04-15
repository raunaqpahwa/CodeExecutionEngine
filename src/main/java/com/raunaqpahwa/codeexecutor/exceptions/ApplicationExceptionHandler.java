package com.raunaqpahwa.codeexecutor.exceptions;

import com.raunaqpahwa.codeexecutor.models.CodeResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({CodeSizeLimitException.class, TimeLimitException.class})
    public ResponseEntity<CodeResult> handleLimitExceptions(Exception exception) {
        CodeResult codeResult = new CodeResult.Builder().appendExceptions(exception.getMessage()).build();
        return new ResponseEntity<>(codeResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ContainerNotCreatedException.class})
    public ResponseEntity<CodeResult> handleContainerNotCreatedException(Exception exception) {
        CodeResult codeResult = new CodeResult.Builder().appendExceptions(exception.getMessage()).build();
        return new ResponseEntity<>(codeResult, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
