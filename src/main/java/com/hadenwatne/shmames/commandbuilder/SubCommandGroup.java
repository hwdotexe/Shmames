package com.hadenwatne.shmames.commandbuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubCommandGroup {
    private String name;
    private String description;
    private List<String> aliases;
    private List<CommandStructure> subcommands;

    public SubCommandGroup(String name, String description) {
        this.name = name;
        this.description = description;
        this.aliases = new ArrayList<>();
        this.subcommands = new ArrayList<>();
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

    public List<CommandStructure> getSubcommands() {
        return this.subcommands;
    }

    public SubCommandGroup addAlias(String alias) {
        this.aliases.add(alias);

        return this;
    }

    public SubCommandGroup addSubCommands(CommandStructure... subCommands){
        this.subcommands.addAll(Arrays.asList(subCommands));

        return this;
    }
}
