package com.hadenwatne.shmames.models.command;

public class ShmamesSubCommandData {
    private String commandName;
    private ShmamesCommandArguments arguments;

    public ShmamesSubCommandData(String name, ShmamesCommandArguments arguments) {
        this.commandName = name;
        this.arguments = arguments;
    }

    public String getAsString() {
        return commandName + " " + arguments.getAsString();
    }

    public String getCommandName() {
        return this.commandName;
    }

    public ShmamesCommandArguments getArguments() {
        return this.arguments;
    }
}
