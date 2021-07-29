package com.hadenwatne.shmames.models.command;

import com.hadenwatne.shmames.commands.ICommand;

public class ParsedCommandResult {
    private ICommand command;
    private String arguments;

    public ParsedCommandResult(ICommand command, String arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    public ICommand getCommand() {
        return this.command;
    }

    public String getArguments() {
        return this.arguments;
    }
}
