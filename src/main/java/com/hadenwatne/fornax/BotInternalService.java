package com.hadenwatne.fornax;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.storage.DatabaseService;
import com.hadenwatne.fornax.storage.models.BotCloudData;
import com.mongodb.client.MongoCursor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.ArrayList;
import java.util.List;

public class BotInternalService {
    private final String CLOUD_DATA_TABLE = "_SYSTEM_";
    private final Bot bot;
    private final BotCloudData cloudData;

    BotInternalService(Bot bot) {
        this.bot = bot;
        this.cloudData = loadBotCloudData();
    }

    JDA authenticate(String apiKey) {
        try {
            return JDABuilder.createDefault(apiKey)
                    .enableCache(CacheFlag.EMOJI)
                    .build();
        } catch (InvalidTokenException e) {
            App.getLogger().LogException(e);
        }

        return null;
    }

    void checkRefreshGlobalCommands(boolean isDebugMode) {
        if (isDebugMode) {
            // Delete global commands.
            bot.getJDA().retrieveCommands().queue(success -> {
                for (net.dv8tion.jda.api.interactions.commands.Command c : success) {
                    bot.getJDA().deleteCommandById(c.getId()).queue();
                }
            });

            // Add all commands to debug mode guilds.
            for (Guild g : bot.getJDA().getGuilds()) {
                CommandListUpdateAction cUpdate = g.updateCommands();

                issueSlashCommandUpdate(cUpdate, bot.getCommands());
            }

            return;
        }

        if (bot.getBotDataStorageService().getBotConfiguration().updateGlobalSlashCommands) {
            // Delete guild commands.
            for (Guild g : bot.getJDA().getGuilds()) {
                g.retrieveCommands().queue(result -> result.forEach(cmd -> g.deleteCommandById(cmd.getId()).queue()));
            }

            // Update global commands.
            CommandListUpdateAction cUpdate = bot.getJDA().updateCommands();
            List<Command> publicCommands = bot.getCommands().stream().filter(Command::isAvailableByDefault).toList();

            issueSlashCommandUpdate(cUpdate, publicCommands);

            // Reactivate individual server commands.
            reactivateGuildCommands();

            bot.getBotDataStorageService().getBotConfiguration().updateGlobalSlashCommands = false;
            bot.getBotDataStorageService().writeBotConfiguration();
        }
    }

    void activateCommandOnGuild(Guild guild, Command command) {
        // Update database
        List<String> activatedCommands = this.cloudData.activatedCommands.getOrDefault(guild.getIdLong(), new ArrayList<>());

        activatedCommands.add(command.getCommandStructure().getName());
        this.cloudData.activatedCommands.put(guild.getId(), activatedCommands);

        bot.getBotDataStorageService().getDatabaseService().updateRecord(BotCloudData.class, CLOUD_DATA_TABLE, "botName", bot.getBotName(), this.cloudData);

        // Issue update
        CommandListUpdateAction cUpdate = guild.updateCommands();
        List<Command> commandToActivate = new ArrayList<>();

        commandToActivate.add(command);
        issueSlashCommandUpdate(cUpdate, commandToActivate);
    }

    private void reactivateGuildCommands() {
        for (String id : this.cloudData.activatedCommands.keySet()) {
            Guild guild = bot.getJDA().getGuildById(id);
            List<Command> commandsToActivate = new ArrayList<>();

            if (guild != null) {
                List<String> activatedCommands = this.cloudData.activatedCommands.get(id);
                CommandListUpdateAction cUpdate = guild.updateCommands();

                for (Command command : bot.getCommands()) {
                    if (activatedCommands.contains(command.getCommandStructure().getName())) {
                        commandsToActivate.add(command);
                    }
                }

                issueSlashCommandUpdate(cUpdate, commandsToActivate);
            }
            // TODO else remove?
        }
    }

    private BotCloudData loadBotCloudData() {
        DatabaseService dbService = bot.getBotDataStorageService().getDatabaseService();
        BotCloudData botCloudData = null;

        try (MongoCursor<BotCloudData> data = dbService.readTable(BotCloudData.class, CLOUD_DATA_TABLE)) {
            while (data.hasNext()) {
                botCloudData = data.next();
            }
        }

        if (botCloudData == null) {
            botCloudData = new BotCloudData(bot.getBotName());
            bot.getBotDataStorageService().getDatabaseService().insertRecord(BotCloudData.class, CLOUD_DATA_TABLE, botCloudData);
        }

        return botCloudData;
    }

    private void issueSlashCommandUpdate(CommandListUpdateAction cUpdate, List<Command> commands) {
        try {
            for (Command command : commands) {
                cUpdate.addCommands(CommandBuilder.BuildSlashCommandData(command));
            }

            cUpdate.queue();
        } catch (Exception e) {
            App.getLogger().LogException(e);
        }
    }
}
