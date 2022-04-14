package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.SubCommandGroup;
import com.hadenwatne.shmames.commands.ICommand;
import com.hadenwatne.shmames.models.command.ParsedCommandResult;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesSubCommandData;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        ParsedCommandResult parsedCommand = App.Shmames.getCommandHandler().parseCommandString(event.getName());

        if (parsedCommand != null) {
            ICommand command = parsedCommand.getCommand();
            ShmamesCommandArguments commandArguments = new ShmamesCommandArguments(buildArgumentMap(command.getCommandStructure(), event));
            ShmamesSubCommandData subCommandData = null;

            // If this is a subcommand, build out the subcommand arguments.
            if(event.getSubcommandGroup() != null) {
                for(SubCommandGroup subCommandGroup : command.getCommandStructure().getSubcommandGroups()) {
                    if(subCommandGroup.getName().equalsIgnoreCase(event.getSubcommandGroup())) {
                        for (CommandStructure subCommand : subCommandGroup.getSubcommands()) {
                            if (subCommand.getName().equalsIgnoreCase(event.getSubcommandName())) {
                                subCommandData = new ShmamesSubCommandData(subCommandGroup.getName(), event.getSubcommandName(), new ShmamesCommandArguments(buildArgumentMap(subCommand, event)));

                                break;
                            }
                        }

                        break;
                    }
                }
            } else if(event.getSubcommandName() != null) {
                for (CommandStructure subCommand : command.getCommandStructure().getSubCommands()) {
                    if(subCommand.getName().equalsIgnoreCase(event.getSubcommandName())) {
                        subCommandData = new ShmamesSubCommandData(event.getSubcommandName(), new ShmamesCommandArguments(buildArgumentMap(subCommand, event)));

                        break;
                    }
                }
            }

            App.Shmames.getCommandHandler().PerformCommand(command, subCommandData, commandArguments, event, event.getGuild());
        }
    }

    private LinkedHashMap<String, Object> buildArgumentMap(CommandStructure structure, SlashCommandEvent event) {
        LinkedHashMap<String, Object> namedArguments = new LinkedHashMap<>();

        for (CommandParameter cp : structure.getParameters()) {
            OptionMapping option = event.getOption(cp.getName().toLowerCase());

            if (option != null) {
                insertArgumentWithType(namedArguments, option, cp);
            }
        }

        return namedArguments;
    }

    private void insertArgumentWithType(HashMap<String, Object> map, OptionMapping option, CommandParameter parameter) {
        switch(parameter.getType()) {
            case BOOLEAN:
                map.put(parameter.getName(), option.getAsBoolean());
                break;
            case INTEGER:
                map.put(parameter.getName(), Integer.parseInt(option.getAsString()));
                break;
            case DISCORD_ROLE:
                map.put(parameter.getName(), option.getAsRole());
                break;
            case DISCORD_USER:
                map.put(parameter.getName(), option.getAsUser());
                break;
            case DISCORD_EMOTE:
                map.put(parameter.getName(), option.getAsMentionable());
                break;
            case DISCORD_CHANNEL:
                map.put(parameter.getName(), option.getAsMessageChannel());
                break;
            default:
                map.put(parameter.getName(), option.getAsString());
        }
    }
}
