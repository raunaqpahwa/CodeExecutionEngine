package com.raunaqpahwa.codeexecutor.models;

import org.apache.commons.text.CaseUtils;

public enum ImageInfo {

    PYTHON("codeengine-python", "latest", "", "3.12"),
    JAVA("codeengine-corretto", "latest", "JDK", "21"),
    JAVASCRIPT("codeengine-node", "latest", "Node", "21.7");

    private final String name;

    private final String tag;

    private final String engine;

    private final String version;

    ImageInfo(String name, String tag, String engine, String version) {
        this.name = name;
        this.tag = tag;
        this.engine = engine;
        this.version = version;
    }

    public String getNameWithTag() {
        return String.format("%s:%s", name, tag);
    }

    public LanguageInfo getLanguageInfo() {
        return new LanguageInfo(CaseUtils.toCamelCase(name(), true), engine, version);
    }
}
