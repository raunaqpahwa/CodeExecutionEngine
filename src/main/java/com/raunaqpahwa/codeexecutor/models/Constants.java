package com.raunaqpahwa.codeexecutor.models;

public class Constants {
    // Generic
    public static final String BASH = "bash";
    public static final String LITERAL = "-c";

    public static final int CODE_SIZE_LIMIT = 1;

    // Java
    public static final String CREATE_JAVA = "CREATE_JAVA";
    public static final String JAVA_WORK_DIR = "/home";
    public static final String JAVA_FILE = "Main.java";
    public static final String JAVA_FILE_COMPILED = "Main";
    public static final String EXECUTE_JAVA = "EXECUTE_JAVA";

    // Python
    public static final String CREATE_PYTHON = "CREATE_PYTHON";
    public static final String EXECUTE_PYTHON = "EXECUTE_PYTHON";
    public static final String PYTHON_WORK_DIR = "/home";
    public static final String PYTHON_FILE = "main.py";

    // Javascript
    public static final String CREATE_JAVASCRIPT = "CREATE_JAVASCRIPT";
    public static final String EXECUTE_JAVASCRIPT = "EXECUTE_JAVASCRIPT";
    public static final String JAVASCRIPT_FILE = "index.js";
    public static final String JAVASCRIPT_WORK_DIR = "/home";
}
