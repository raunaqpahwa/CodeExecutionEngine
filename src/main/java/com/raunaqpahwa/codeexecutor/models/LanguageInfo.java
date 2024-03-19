package com.raunaqpahwa.codeexecutor.models;

public class LanguageInfo {

    private final String languageName;

    private final String engine;

    private final String version;

    LanguageInfo(String languageName, String engine, String version) {
        this.languageName = languageName;
        this.engine = engine;
        this.version = version;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getEngine() {
        return engine;
    }

    public String getVersion() {
        return version;
    }
}
