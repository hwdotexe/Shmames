package com.hadenwatne.botcore.command.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParameter implements Cloneable {
    private final String name;
    private String example;
    private final String description;
    private final Boolean isRequired;
    private final ParameterType type;
    private final List<String> selectionOptions;

    public CommandParameter(String name, String description, ParameterType type) {
        this.name = name;
        this.description = description;
        this.isRequired = true;
        this.type = type;
        this.selectionOptions = new ArrayList<>();
    }

    public CommandParameter(String name, String description, ParameterType type, Boolean isRequired) {
        this.name = name;
        this.description = description;
        this.isRequired = isRequired;
        this.type = type;
        this.selectionOptions = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getExample() {
        return this.example;
    }

    public CommandParameter setExample(String example) {
        this.example = example;

        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isRequired() {
        return this.isRequired;
    }

    public ParameterType getType() {
        return this.type;
    }

    public List<String> getSelectionOptions() {
        return this.selectionOptions;
    }

    public CommandParameter addSelectionOptions(String... options) {
        this.selectionOptions.addAll(Arrays.asList(options));

        return this;
    }
}
