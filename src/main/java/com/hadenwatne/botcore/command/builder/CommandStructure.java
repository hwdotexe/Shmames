package com.hadenwatne.botcore.command.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandStructure {
    private final String name;
    private final String description;
    private final List<CommandParameter> parameters;
    private final List<CommandStructure> subCommands;
    private final List<SubCommandGroup> subCommandGroups;

    private String usage;
    private String examples;

    public CommandStructure(String name, String description) {
        this.name = name;
        this.description = description;
        this.parameters = new ArrayList<>();
        this.subCommands = new ArrayList<>();
        this.subCommandGroups = new ArrayList<>();
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

    public List<CommandParameter> getParameters() {
        return this.parameters;
    }

    public CommandStructure addParameters(CommandParameter... parameters) {
        Collections.addAll(this.parameters, parameters);

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
        this.usage = CommandBuilder.BuildUsage(this, false);
        this.examples = CommandBuilder.BuildExample(this);

        return this;
    }
}
