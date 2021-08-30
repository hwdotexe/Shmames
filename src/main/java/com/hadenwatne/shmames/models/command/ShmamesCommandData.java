package com.hadenwatne.shmames.models.command;

import com.hadenwatne.shmames.commands.ICommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ShmamesCommandData {
    private ICommand command;
    private ShmamesCommandArguments arguments;
    private ShmamesSubCommandData subCommand;
    private ShmamesCommandMessagingChannel messagingChannel;
    private User author;
    private @Nullable Guild server;

    public ShmamesCommandData(ICommand command, ShmamesCommandArguments arguments, ShmamesCommandMessagingChannel messagingChannel, User author, @Nullable Guild server) {
        this.command = command;
        this.arguments = arguments;
        this.messagingChannel = messagingChannel;
        this.author = author;
        this.server = server;
    }

    public ShmamesCommandData(ICommand command, ShmamesSubCommandData subCommands, ShmamesCommandArguments arguments, ShmamesCommandMessagingChannel messagingChannel, User author, @Nullable Guild server) {
        this.command = command;
        this.subCommand = subCommands;
        this.arguments = arguments;
        this.messagingChannel = messagingChannel;
        this.author = author;
        this.server = server;
    }

    public @Nullable Guild getServer() {
        return this.server;
    }

    public ICommand getCommand() {
        return this.command;
    }

    public ShmamesCommandArguments getArguments() {
        return this.arguments;
    }

    public ShmamesSubCommandData getSubCommand() {
        return this.subCommand;
    }

    public ShmamesCommandMessagingChannel getMessagingChannel() {
        return this.messagingChannel;
    }

    public User getAuthor() {
        return this.author;
    }
}
