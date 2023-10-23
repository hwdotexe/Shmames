package com.hadenwatne.botcore;

import com.hadenwatne.botcore.listener.CommandListener;
import com.hadenwatne.botcore.storage.StorageService;
import com.hadenwatne.botcore.utility.BotUtility;
import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.shmames.enums.LogType;
import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;
import java.util.List;

public abstract class Bot {
    private final JDA _jda;
    private final StorageService _storageService;
    private final List<Command> _commands;
    private String _botName;
    private String _botAvatarUrl;

    public Bot() {
        App.getLogger().Log(LogType.SYSTEM, "Bot is initializing!");

        // Instantiate
        _commands = new ArrayList<>();

        // Services
        _storageService = new StorageService();

        // Start JDA
        _jda = BotUtility.authenticate(_storageService.getBotConfiguration().botApiToken);
        startJDA();
        populateBotInfo();

        // Commands
        registerCommands();
        BotUtility.updateSlashCommands(isDebugMode(), this);
        _jda.addEventListener(new CommandListener(this));

        // Call optional provided initialize method
        afterInit();

        App.getLogger().Log(LogType.SYSTEM, "Bot is ready!");
    }

    public final boolean isDebugMode() {
        return App.isDebugMode();
    }

    public final String getBotName() {
        return _botName;
    }

    public final String getBotAvatarUrl() {
        return _botAvatarUrl;
    }

    public final JDA getJDA() {
        return _jda;
    }

    public final List<Command> getCommands() {
        return _commands;
    }

    public final StorageService getStorageService() {
        return _storageService;
    }

    protected final void registerCommand(Command command) {
        _commands.add(command);
    }

    protected abstract void afterInit();

    protected abstract void registerCommands();

    private void startJDA() {
        try {
            _jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateBotInfo() {
        _botName = _jda.getSelfUser().getName();
        _botAvatarUrl = _jda.getSelfUser().getAvatarUrl();
    }
}
