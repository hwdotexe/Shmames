package com.hadenwatne.shmames.commandbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandParameter {
    private String name;
    private String description;
    private Boolean isRequired;
    private ParameterType type;
    private Pattern matchPattern;
    private List<String> selectionOptions;

    public CommandParameter(String name, String description, ParameterType type) {
        this.name = name;
        this.description = description;
        this.isRequired = true;
        this.type = type;
        this.selectionOptions = new ArrayList<>();

        setInitialMatchPattern();
    }

    public CommandParameter(String name, String description, ParameterType type, Boolean isRequired) {
        this.name = name;
        this.description = description;
        this.isRequired = isRequired;
        this.type = type;
        this.selectionOptions = new ArrayList<>();

        setInitialMatchPattern();
    }

    public CommandParameter(String name, String description, ParameterType type, Boolean isRequired, Pattern pattern) {
        this.name = name;
        this.description = description;
        this.isRequired = isRequired;
        this.type = type;
        this.selectionOptions = new ArrayList<>();
        this.matchPattern = pattern;
    }

    public CommandParameter(String name, String description, ParameterType type, Boolean isRequired, Pattern pattern, List<String> selectionOptions) {
        this.name = name;
        this.description = description;
        this.isRequired = isRequired;
        this.type = type;
        this.selectionOptions = selectionOptions;
        this.matchPattern = pattern;
    }

    public String getName() {
        return this.name;
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

    public Pattern getPattern() {
        return this.matchPattern;
    }

    public CommandParameter setPattern(String pattern) {
        this.matchPattern = Pattern.compile("(?<"+this.name+">"+pattern+")");

        return this;
    }

    public List<String> getSelectionOptions() {
        return this.selectionOptions;
    }

    public CommandParameter addSelectionOptions(String... options) {
        this.selectionOptions.addAll(Arrays.asList(options));

        return this;
    }

    private void setInitialMatchPattern() {
        switch(this.type) {
            case INTEGER:
                this.matchPattern = Pattern.compile("(?<"+this.name+">\\d+)");
                break;
            case DISCORD_ROLE:
                this.matchPattern = Pattern.compile("(?<"+this.name+"><@&\\d+>)");
                break;
            case DISCORD_CHANNEL:
                this.matchPattern = Pattern.compile("(?<"+this.name+"><#\\d+>)");
                break;
            case DISCORD_USER:
                this.matchPattern = Pattern.compile("(?<"+this.name+"><@!\\d+>)");
                break;
            case DISCORD_EMOTE:
                this.matchPattern = Pattern.compile("(?<"+this.name+"><:[a-zA-Z0-9_]:\\d+>)");
                break;
            default:
                this.matchPattern = Pattern.compile("(?<"+this.name+">.+)");
        }
    }
}
