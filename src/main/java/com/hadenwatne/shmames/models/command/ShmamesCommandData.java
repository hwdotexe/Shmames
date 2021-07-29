package com.hadenwatne.shmames.models.command;

import com.hadenwatne.shmames.commands.ICommand;
import net.dv8tion.jda.api.entities.User;

public class ShmamesCommandData {
    private ICommand command;
    private ShmamesCommandArguments arguments;
    private CommandMessagingChannel messagingChannel;
    private User author;

    public ShmamesCommandData(ICommand command, ShmamesCommandArguments arguments, CommandMessagingChannel messagingChannel, User author) {
        this.command = command;
        this.arguments = arguments;
        this.messagingChannel = messagingChannel;
        this.author = author;
    }

    public ICommand getCommand() {
        return this.command;
    }

    public ShmamesCommandArguments getArguments() {
        return this.arguments;
    }

    public CommandMessagingChannel getMessagingChannel() {
        return this.messagingChannel;
    }

    public User getAuthor() {
        return this.author;
    }
}
