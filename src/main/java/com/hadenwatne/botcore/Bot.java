package com.hadenwatne.botcore;

import com.hadenwatne.botcore.listener.CommandListener;
import com.hadenwatne.botcore.service.DefaultLanguageProvider;
import com.hadenwatne.botcore.service.ILanguageProvider;
import com.hadenwatne.botcore.storage.BotConfigService;
import com.hadenwatne.botcore.utility.BotUtility;
import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.service.types.LogType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class Bot {
    private final JDA _jda;
    private final BotConfigService _botConfigService;
    private ILanguageProvider _languageProvider;
    private final List<Command> _commands;
    private String _botName;
    private String _botAvatarUrl;

    public Bot() {
        App.getLogger().Log(LogType.SYSTEM, "Bot is initializing!");

        // Instantiate
        _commands = new ArrayList<>();

        // Services
        _botConfigService = new BotConfigService();
        _languageProvider = new DefaultLanguageProvider();

        // Start JDA
        _jda = BotUtility.authenticate(_botConfigService.getBotConfiguration().botApiToken);
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

    public final BotConfigService getBotConfigService() {
        return _botConfigService;
    }

    public ILanguageProvider getLanguageProvider() {
        return _languageProvider;
    }

    protected final void registerCommand(Command command) {
        _commands.add(command);
    }

    protected final void registerListener(EventListener listener) {
        this._jda.addEventListener(listener);
    }

    protected final void registerLanguageProvider(ILanguageProvider languageProvider) {
        this._languageProvider = languageProvider;
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
