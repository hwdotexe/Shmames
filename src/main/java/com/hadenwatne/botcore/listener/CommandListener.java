package com.hadenwatne.botcore.listener;

import com.hadenwatne.botcore.App;
import com.hadenwatne.botcore.Bot;
import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.Execution;
import com.hadenwatne.botcore.type.ExecutionFailReason;
import com.hadenwatne.botcore.type.ExecutionStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.CompletableFuture;

public class CommandListener extends ListenerAdapter {
    private Bot _bot;

    public CommandListener(Bot bot) {
        _bot = bot;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Command command = matchCommandObject(event.getName());

        if (command != null) {
            // Gets the server language [Bot Handled]

            Execution execution = new Execution(command, event);

            CompletableFuture.supplyAsync(() -> {
                        boolean hasPermission = checkBotCommandPermissions(execution);

                        if (hasPermission) {
                            execution.setStatus(ExecutionStatus.RUNNING);

                            // TODO tally the command usage [Core Handled]

                            return command.run(execution);
                        } else {
                            execution.setStatus(ExecutionStatus.FAILED);
                            execution.setFailureReason(ExecutionFailReason.BOT_MISSING_PERMISSION);
                            execution.setEphemeral(true);

                            return null;
                            // TODO reply with a no-permission message
                            // TODO should we have an injectable service for error handling?
                            // TODO error event dispatched, listened to in a listener?
                        }
                    }).thenAccept(result -> {
                        if (result != null) {
                            execution.reply(result);
                        }

                        execution.setStatus(ExecutionStatus.COMPLETE);
                    })
                    .exceptionally(exception -> {
                        execution.setStatus(ExecutionStatus.FAILED);
                        execution.setFailureReason(ExecutionFailReason.EXCEPTION_CAUGHT);
                        App.getLogger().LogException(exception);
                        return null;
                    });
        }
    }

    private boolean checkBotCommandPermissions(Execution execution) {
        if (execution.isFromServer()) {
            Guild server = execution.getServer();
            GuildMessageChannel channel = execution.getChannel().asGuildMessageChannel();

            for (Permission p : execution.getCommand().getRequiredPermissions()) {
                if (!server.getSelfMember().hasPermission(channel, p)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Command matchCommandObject(String name) {
        for (Command command : _bot.getCommands()) {
            if (command.getCommandStructure().getName().equalsIgnoreCase(name)) {
                return command;
            }
        }

        return null;
    }
}