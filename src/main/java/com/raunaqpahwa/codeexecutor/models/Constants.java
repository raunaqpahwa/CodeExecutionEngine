package com.raunaqpahwa.codeexecutor.models;

public class Constants {
    // Generic
    public static final String BASH = "bash";
    public static final String LITERAL = "-c";
    public static final int CODE_SIZE_LIMIT = 1;

    // Java
    public static final String JAVA = "JAVA";
    public static final String JAVA_WORK_DIR = "/home";
    public static final String JAVA_FILE = "Main.java";
    public static final String JAVA_FILE_COMPILED = "Main";
    public static final String EXECUTE_JAVA = "EXECUTE_JAVA";

    // Python
    public static final String PYTHON = "PYTHON";
    public static final String EXECUTE_PYTHON = "EXECUTE_PYTHON";
    public static final String PYTHON_WORK_DIR = "/home";
    public static final String PYTHON_FILE = "main.py";

    // Javascript
    public static final String JAVASCRIPT = "JAVASCRIPT";
    public static final String EXECUTE_JAVASCRIPT = "EXECUTE_JAVASCRIPT";
    public static final String JAVASCRIPT_FILE = "index.js";
    public static final String JAVASCRIPT_WORK_DIR = "/home";

    // Exceptions
    public static final String CODE_SIZE_LIMIT_EXCEPTION = "The code size is too long to be executed. Please reduce the lines of code and try again.";
    public static final String TIME_LIMIT_EXCEPTION = "Your code failed to execute in the permitted time of %d seconds.";
    public static final String CONTAINER_NOT_CREATED_EXCEPTION = "The server is busy, the docker container could not be created.";
    public static final String LANGUAGE_UNSUPPORTED = "This programming language is not supported.";
}
