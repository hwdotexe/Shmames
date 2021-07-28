package com.hadenwatne.shmames.commandbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandParameter {
    private final String name;
    private final String description;
    private final Boolean isRequired;
    private final ParameterType type;
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

        setInitialMatchPattern();

        return this;
    }

    private void setInitialMatchPattern() {
        switch(this.type) {
            case SELECTION:
                StringBuilder sb = new StringBuilder();
                StringBuilder psb = new StringBuilder();

                sb.append("(");

                for(String o : this.selectionOptions) {
                    if(psb.length() > 0) {
                        psb.append("|");
                    }

                    psb.append("(");
                    psb.append(o);
                    psb.append(")");
                }

                sb.append(psb);
                sb.append(")");

                this.matchPattern = Pattern.compile("(?<"+this.name+">"+sb+")");
                break;
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
