package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commands.ICommand;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        ICommand command = getCommandFromName(event.getName());

        if(command != null) {
            HashMap<String, Object> namedArguments = new HashMap<>();

            for(CommandParameter cp : command.getCommandStructure().getParameters()) {
                OptionMapping option = event.getOption(cp.getName().toLowerCase());

                if(option != null) {
                    insertArgumentWithType(namedArguments, option, cp);
                }
            }

            ShmamesCommandArguments sca = new ShmamesCommandArguments(namedArguments);

            Shmames.getCommandHandler().PerformCommand(command, sca, event, event.getGuild());
        }
    }

    private ICommand getCommandFromName(String name) {
        for(ICommand c : Shmames.getCommandHandler().getLoadedCommands()) {
            if(c.getCommandStructure().getName().equalsIgnoreCase(name)) {
                return c;
            }
        }

        return null;
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
