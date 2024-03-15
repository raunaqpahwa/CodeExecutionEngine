package com.raunaqpahwa.codeexecutor.models;

public enum ImageInfo {


    PYTHON("python", "3.12-slim"),
    JAVA("amazoncorretto", "21.0.2"),
    JAVASCRIPT("node", "21.7");
    private final String name;

    private final String tag;

    ImageInfo(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getNameWithTag() {
        return String.format("%s:%s", getName(), getTag());
    }
}
