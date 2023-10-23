package com.hadenwatne.botcore;

import com.hadenwatne.botcore.storage.StorageService;
import com.hadenwatne.botcore.utility.BotUtility;
import com.hadenwatne.shmames.commands.Command;
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
        // Instantiate
        _commands = new ArrayList<>();

        // Services
        _storageService = new StorageService();

        initialize();

        // Start JDA
        _jda = BotUtility.authenticate(_storageService.getBotConfiguration().botApiToken);
        startJDA();
        populateBotInfo();

        // Commands
        registerCommands();
    }

    public boolean isDebugMode() {
        return App.isDebugMode();
    }

    public String getBotName() {
        return _botName;
    }

    public String getBotAvatarUrl() {
        return _botAvatarUrl;
    }

    protected final void registerCommand(Command command) {
        _commands.add(command);
    }

    protected abstract void initialize();

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
