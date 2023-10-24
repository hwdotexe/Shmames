package com.hadenwatne.botcore.command.builder;

import com.hadenwatne.botcore.command.builder.types.ParameterType;
import com.hadenwatne.shmames.enums.RegexPatterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandParameter implements Cloneable {
    private final String name;
    private String example;
    private final String description;
    private final Boolean isRequired;
    private final ParameterType type;
    private String customPattern;
    private Pattern matchPattern;
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

    public Pattern getPattern() {
        return this.matchPattern;
    }

    public CommandParameter setPattern(String pattern) {
        this.customPattern = pattern;

        return this;
    }

    public List<String> getSelectionOptions() {
        return this.selectionOptions;
    }

    public CommandParameter addSelectionOptions(String... options) {
        this.selectionOptions.addAll(Arrays.asList(options));

        return this;
    }

    public void buildRegexPattern() {
        if (this.customPattern != null) {
            this.matchPattern = Pattern.compile("(?<" + this.name + ">" + this.customPattern + ")");

            return;
        }

        // TODO do we need regex validation for this if Discord does it for us? (no)
        switch (this.type) {
            case SELECTION:
                StringBuilder sb = new StringBuilder();
                StringBuilder psb = new StringBuilder();

                sb.append("(");

                for (String o : this.selectionOptions) {
                    if (psb.length() > 0) {
                        psb.append("|");
                    }

                    psb.append("(");
                    psb.append(o);
                    psb.append(")");
                }

                sb.append(psb);
                sb.append(")");

                this.matchPattern = Pattern.compile("(?<" + this.name + ">" + sb + ")");
                break;
                // TODO can this be a selection?
            case BOOLEAN:
                this.matchPattern = Pattern.compile("(?<" + this.name + ">((true)|(false)))");
                break;
            case TIMECODE:
                this.matchPattern = Pattern.compile("(?<" + this.name + ">[\\dydhms]+)");
                break;
            case INTEGER:
                this.matchPattern = Pattern.compile("(?<" + this.name + ">\\d+)");
                break;
            case DISCORD_ROLE:
                this.matchPattern = Pattern.compile("(?<" + this.name + ">(\\d+))");
                break;
            case DISCORD_CHANNEL:
                this.matchPattern = Pattern.compile("(?<" + this.name + "><#(\\d+)>)");
                break;
            case DISCORD_USER:
                this.matchPattern = Pattern.compile("(?<" + this.name + "><@!(\\d+)>)");
                break;
            case DISCORD_EMOTE:
                this.matchPattern = Pattern.compile("(?<" + this.name + ">" + RegexPatterns.EMOTE + ")");
                break;
            default:
                this.matchPattern = Pattern.compile("(?<" + this.name + ">.+)");
        }
    }
}