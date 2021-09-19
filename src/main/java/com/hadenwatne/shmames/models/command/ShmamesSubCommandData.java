package com.hadenwatne.shmames.models.command;

public class ShmamesSubCommandData {
    private final String groupName;
    private final String commandName;
    private final ShmamesCommandArguments arguments;

    public ShmamesSubCommandData(String groupName, String commandName, ShmamesCommandArguments arguments) {
        this.groupName = groupName;
        this.commandName = commandName;
        this.arguments = arguments;
    }

    public ShmamesSubCommandData(String commandName, ShmamesCommandArguments arguments) {
        this.groupName = null;
        this.commandName = commandName;
        this.arguments = arguments;
    }

    public String getAsString() {
        return (groupName != null ? (groupName + " ") : "") + commandName + " " + arguments.getAsString();
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public String getNameOrGroup() {
        if(this.groupName != null) {
            return this.groupName;
        }

        return this.commandName;
    }

    public ShmamesCommandArguments getArguments() {
        return this.arguments;
    }
}