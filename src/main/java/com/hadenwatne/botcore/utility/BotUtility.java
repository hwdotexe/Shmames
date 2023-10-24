package com.hadenwatne.botcore.utility;

import com.hadenwatne.botcore.App;
import com.hadenwatne.botcore.Bot;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.Command;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.List;

public class BotUtility {
    public static JDA authenticate(String apiKey) {
        try {
            return JDABuilder.createDefault(apiKey)
                    .enableCache(CacheFlag.EMOJI)
                    .build();
        } catch (InvalidTokenException e) {
            App.getLogger().LogException(e);
        }

        return null;
    }

    public static void updateSlashCommands(boolean isDebugMode, Bot bot) {
        if(isDebugMode) {
            // Delete global commands.
            bot.getJDA().retrieveCommands().queue(success -> {
                for(net.dv8tion.jda.api.interactions.commands.Command c : success) {
                    bot.getJDA().deleteCommandById(c.getId()).queue();
                }
            });

            // Update guild commands.
            for(Guild g : bot.getJDA().getGuilds()) {
                CommandListUpdateAction cUpdate = g.updateCommands();

                issueSlashCommandUpdate(cUpdate, bot.getCommands());
            }

            return;
        }

        if(bot.getBotDataStorageService().getBotConfiguration().updateGlobalSlashCommands) {
            // Delete guild commands.
            for(Guild g : bot.getJDA().getGuilds()) {
                g.retrieveCommands().queue(result -> result.forEach(cmd -> g.deleteCommandById(cmd.getId()).queue()));
            }

            // Update global commands.
            CommandListUpdateAction cUpdate = bot.getJDA().updateCommands();

            issueSlashCommandUpdate(cUpdate, bot.getCommands());

            // TODO issue an update for each server that has a command activated that isn't available by default

            bot.getBotDataStorageService().getBotConfiguration().updateGlobalSlashCommands = false;
            bot.getBotDataStorageService().writeBotConfiguration();
        }
    }

    private static void issueSlashCommandUpdate(CommandListUpdateAction cUpdate, List<Command> commands) {
        try {
            for (Command command : commands) {
                if (command.isAvailableByDefault()) {
                    cUpdate.addCommands(CommandBuilder.BuildSlashCommandData(command));
                }
            }

            cUpdate.queue();
        }catch (Exception e) {
            App.getLogger().LogException(e);
        }
    }
}
