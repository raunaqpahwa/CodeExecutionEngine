package com.raunaqpahwa.codeexecutor.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CodeResult {
    private String standardOutput;
    private String standardError;
    private String exceptions;

    public String getStandardOutput() {
        return standardOutput;
    }

    public void setStandardOutput(String standardOutput) {
        this.standardOutput = standardOutput;
    }

    public String getStandardError() {
        return standardError;
    }

    public void setStandardError(String standardError) {
        this.standardError = standardError;
    }

    public String getExceptions() {
        return exceptions;
    }

    public void setExceptions(String exceptions) {
        this.exceptions = exceptions;
    }
}
