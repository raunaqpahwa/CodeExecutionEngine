package com.raunaqpahwa.codeexecutor.models;

import com.github.dockerjava.api.model.Container;

public class DockerContainer {

    public enum Language {
        JAVA,
        PYTHON,
        JAVASCRIPT;
    }

    // Do not change the order of the enum Removal Priority as the comparison takes the order in account
    public enum RemovalPriority {
        HIGH,
        LOW
    }
    private final Container container;

    private final Language language;

    private RemovalPriority removalPriority;

    public DockerContainer(Container container, Language language, RemovalPriority removalPriority) {
        this.container = container;
        this.language = language;
        this.removalPriority = removalPriority;
    }

    public Container getContainer() {
        return container;
    }

    public Language getLanguage() {
        return language;
    }

    public RemovalPriority getRemovalPriority() {
        return removalPriority;
    }

    @Override
    public String toString() {
        return String.format("Container: %s | Language: %s | Removal Priority: %s\n", getContainer(), getLanguage().name(), getRemovalPriority().name());
    }

    public void setRemovalPriority(RemovalPriority removalPriority) {
        this.removalPriority = removalPriority;
    }
}
