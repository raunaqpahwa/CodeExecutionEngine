package com.raunaqpahwa.codeexecutor.models;

public enum ImageInfo {

    PYTHON("codeengine-python", "latest"),
    JAVA("codeengine-corretto", "latest"),
    JAVASCRIPT("codeengine-node", "latest");
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
