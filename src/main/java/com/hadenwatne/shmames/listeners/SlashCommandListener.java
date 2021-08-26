package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commands.ICommand;
import com.hadenwatne.shmames.models.command.ParsedCommandResult;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        ParsedCommandResult parsedCommand = Shmames.getCommandHandler().parseCommandString(event.getName());

        if (parsedCommand != null) {
            ICommand command = parsedCommand.getCommand();

            LinkedHashMap<String, Object> namedArguments = new LinkedHashMap<>();

            // TODO if the user does not TAB to name the parameter, it does not get sent with a name (with optional param)
            // TODO Default to the first optional parameter if this is the case?
            // TODO This might be a Library limitation
            for (CommandParameter cp : command.getCommandStructure().getParameters()) {
                OptionMapping option = event.getOption(cp.getName().toLowerCase());

                if (option != null) {
                    insertArgumentWithType(namedArguments, option, cp);
                }
            }

            ShmamesCommandArguments sca = new ShmamesCommandArguments(namedArguments);

            Shmames.getCommandHandler().PerformCommand(command, sca, event, event.getGuild());
        }
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
