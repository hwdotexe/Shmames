package com.hadenwatne.botcore.listener;

import com.hadenwatne.botcore.App;
import com.hadenwatne.botcore.Bot;
import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.Execution;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.SubCommandGroup;
import com.hadenwatne.botcore.command.types.ExecutionFailReason;
import com.hadenwatne.botcore.command.types.ExecutionStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

public class CommandListener extends ListenerAdapter {
    private Bot _bot;

    public CommandListener(Bot bot) {
        _bot = bot;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Command command = matchCommandObject(event.getName());

        if (command != null) {
            Execution execution = new Execution(_bot, command, event);

            CompletableFuture.supplyAsync(() -> {
                        boolean hasPermission = checkBotCommandPermissions(execution);

                        if (hasPermission) {
                            execution.setStatus(ExecutionStatus.RUNNING);

                            boolean parametersMatch = matchParameterRegex(execution);

                            if (parametersMatch) {
                                command.run(execution);

                                return true;
                            } else {
                                execution.setFailureReason(ExecutionFailReason.COMMAND_USAGE_INCORRECT);
                            }
                        } else {
                            execution.setFailureReason(ExecutionFailReason.BOT_MISSING_PERMISSION);
                        }

                        execution.setStatus(ExecutionStatus.FAILED);
                        command.onCommandFailure(execution);

                        return false;
                    }).thenAccept(result -> {
                        if (result) {
                            execution.setStatus(ExecutionStatus.COMPLETE);
                        }
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

    private boolean matchParameterRegex(Execution execution) {
        // TODO: this code is terrible please fix
        // TODO does it only need to apply to strings?

        CommandStructure structure = execution.getCommand().getCommandStructure();

        if (execution.getSubCommandGroup() != null) {
            for (SubCommandGroup group : structure.getSubcommandGroups()) {
                if (group.getName().equalsIgnoreCase(execution.getSubCommandGroup())) {
                    for (CommandStructure subCommand : group.getSubCommands()) {
                        if (subCommand.getName().equalsIgnoreCase(execution.getSubCommand())) {
                            for (CommandParameter parameter : subCommand.getParameters()) {
                                if (parameter.isRequired() || execution.getArguments().containsKey(parameter.getName())) {
                                    // Match the argument against the parameter's expected pattern.
                                    Matcher matcher = parameter.getPattern().matcher(execution.getArguments().get(parameter.getName()).getAsString());

                                    if (!matcher.find()) {
                                        return false;
                                    }
                                }
                            }

                            break;
                        }
                    }

                    break;
                }
            }
        } else if (execution.getSubCommand() != null) {
            for (CommandStructure subCommand : structure.getSubCommands()) {
                if (subCommand.getName().equalsIgnoreCase(execution.getSubCommand())) {
                    for (CommandParameter parameter : subCommand.getParameters()) {
                        if (parameter.isRequired() || execution.getArguments().containsKey(parameter.getName())) {
                            // Match the argument against the parameter's expected pattern.
                            Matcher matcher = parameter.getPattern().matcher(execution.getArguments().get(parameter.getName()).getAsString());

                            if (!matcher.find()) {
                                return false;
                            }
                        }
                    }

                    break;
                }
            }
        } else {
            for (CommandParameter parameter : structure.getParameters()) {
                if (parameter.isRequired() || execution.getArguments().containsKey(parameter.getName())) {
                    // Match the argument against the parameter's expected pattern.
                    Matcher matcher = parameter.getPattern().matcher(execution.getArguments().get(parameter.getName()).getAsString());

                    if (!matcher.find()) {
                        return false;
                    }
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