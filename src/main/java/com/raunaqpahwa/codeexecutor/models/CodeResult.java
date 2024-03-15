package com.raunaqpahwa.codeexecutor.models;

public class CodeResult {
    private String stdout;
    private String stderr;
    private String exceptions;

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public String getExceptions() {
        return exceptions;
    }

    public static class Builder {

        private final StringBuilder stdout = new StringBuilder();
        private final StringBuilder stderr = new StringBuilder();
        private final StringBuilder exceptions = new StringBuilder();

        public Builder appendStdout(String s) {
            stdout.append(s);
            return this;
        }

        public Builder appendStderr(String s) {
            stderr.append(s);
            return this;
        }

        public Builder appendExceptions(String s) {
            exceptions.append(s);
            return this;
        }

        public Builder clear() {
            stdout.setLength(0);
            stderr.setLength(0);
            exceptions.setLength(0);
            return this;
        }

        public CodeResult build() {
            var codeResult = new CodeResult();
            codeResult.stdout = stdout.toString();
            codeResult.stderr = stderr.toString();
            codeResult.exceptions = exceptions.toString();
            return codeResult;
        }
    }
}
