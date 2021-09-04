package com.hadenwatne.shmames.commandbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CommandStructure {
    private String name;
    private String description;
    private List<String> aliases;
    private List<CommandParameter> parameters;
    private Pattern pattern;
    private String usage;
    private List<CommandStructure> subcommands;
    private List<SubCommandGroup> subCommandGroups;
    private boolean isOptional;

    public CommandStructure(String name, String description) {
        this.name = name;
        this.description = description;
        this.aliases = new ArrayList<>();
        this.parameters = new ArrayList<>();
        this.subcommands = new ArrayList<>();
        this.isOptional = false;
    }

    public Pattern getPattern() {
        return this.pattern;
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

    public List<String> getAliases() {
        return this.aliases;
    }

    public boolean isOptional() {
        return this.isOptional;
    }

    public CommandStructure setOptional(boolean optional) {
        this.isOptional = optional;

        return this;
    }

    public CommandStructure addAlias(String alias) {
        this.aliases.add(alias);

        return this;
    }

    public List<CommandParameter> getParameters() {
        return this.parameters;
    }

    public CommandStructure addParameter(CommandParameter parameter) {
        this.parameters.add(parameter);

        return this;
    }

    public CommandStructure addParameters(CommandParameter... parameters) {
        Collections.addAll(this.parameters, parameters);

        return this;
    }

    public List<CommandStructure> getSubcommands() {
        return this.subcommands;
    }

    public CommandStructure addSubCommands(CommandStructure... subcommand){
        this.subcommands.addAll(Arrays.asList(subcommand));

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
        this.pattern = CommandBuilder.BuildPattern(this);
        this.usage = CommandBuilder.BuildUsage(this);

        return this;
    }
}
