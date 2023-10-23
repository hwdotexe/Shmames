package com.hadenwatne.botcore.command.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubCommandGroup {
    private final String name;
    private final String description;
    private final List<CommandStructure> subcommands;

    public SubCommandGroup(String name, String description) {
        this.name = name;
        this.description = description;
        this.subcommands = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<CommandStructure> getSubCommands() {
        return this.subcommands;
    }

    public SubCommandGroup addSubCommands(CommandStructure... subCommands){
        this.subcommands.addAll(Arrays.asList(subCommands));

        return this;
    }
}
