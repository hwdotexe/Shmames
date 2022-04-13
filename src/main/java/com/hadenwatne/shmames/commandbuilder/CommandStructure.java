package com.hadenwatne.shmames.commandbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandStructure {
    private String name;
    private String description;
    private List<String> aliases;
    private List<CommandParameter> parameters;
    private Pattern pattern;
    private String usage;
    private List<CommandStructure> subCommands;
    private List<SubCommandGroup> subCommandGroups;

    public CommandStructure(String name, String description) {
        this.name = name;
        this.description = description;
        this.aliases = new ArrayList<>();
        this.parameters = new ArrayList<>();
        this.subCommands = new ArrayList<>();
        this.subCommandGroups = new ArrayList<>();
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

        this.pattern = CommandBuilder.BuildPattern(this);
        this.usage = CommandBuilder.BuildUsage(this, false);

        return this;
    }
}
