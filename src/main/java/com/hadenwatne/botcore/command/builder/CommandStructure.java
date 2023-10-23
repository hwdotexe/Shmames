package com.hadenwatne.botcore.command.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandStructure {
    private final String name;
    private final String description;
    private final List<String> aliases;
    private final List<CommandParameter> parameters;
    private final List<CommandStructure> subCommands;
    private final List<SubCommandGroup> subCommandGroups;

    private Pattern primaryPattern;
    private Pattern contextPattern;
    private Pattern matcherPattern;
    private String usage;
    private String examples;

    public CommandStructure(String name, String description) {
        this.name = name;
        this.description = description;
        this.aliases = new ArrayList<>();
        this.parameters = new ArrayList<>();
        this.subCommands = new ArrayList<>();
        this.subCommandGroups = new ArrayList<>();
    }

    public Pattern getPrimaryPattern() {
        return this.primaryPattern;
    }

    public Pattern getContextPattern() {
        return this.contextPattern;
    }

    public Pattern getMatcherPattern() {
        return this.matcherPattern;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getExamples() {
        return this.examples;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public CommandStructure addAlias(String alias) {
        this.aliases.add(alias);

        return this;
    }

    public List<CommandParameter> getParameters() {
        return this.parameters;
    }

    public CommandStructure addParameters(CommandParameter... parameters) {
        for(CommandParameter parameter : parameters) {
            CommandParameter clone = parameter.clone();

            clone.setRegexName(parameter.getName()+this.name);

            this.parameters.add(clone);
        }

        return this;
    }

    public List<CommandStructure> getSubCommands() {
        return this.subCommands;
    }

    public CommandStructure addSubCommands(CommandStructure... subcommand){
        this.subCommands.addAll(Arrays.asList(subcommand));

        return this;
    }

    public List<SubCommandGroup> getSubcommandGroups() {
        return this.subCommandGroups;
    }

    public CommandStructure addSubCommandGroups(SubCommandGroup... subCommandGroups){
        this.subCommandGroups.addAll(Arrays.asList(subCommandGroups));

        return this;
    }

    public CommandStructure build() {
        for(CommandParameter parameter : this.parameters) {
            parameter.buildRegexPattern();
        }

        this.primaryPattern = CommandBuilder.BuildPrimaryPattern(this);
        this.contextPattern = CommandBuilder.BuildContextPattern(this);
        this.matcherPattern = CommandBuilder.BuildMatcherPattern(this);
        this.usage = CommandBuilder.BuildUsage(this, false);
        this.examples = CommandBuilder.BuildExample(this);

        return this;
    }
}
