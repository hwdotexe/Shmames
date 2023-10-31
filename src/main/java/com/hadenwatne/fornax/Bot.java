package com.hadenwatne.fornax;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.listener.CommandListener;
import com.hadenwatne.fornax.listener.InteractionListener;
import com.hadenwatne.fornax.service.DefaultLanguageProvider;
import com.hadenwatne.fornax.service.ILanguageProvider;
import com.hadenwatne.fornax.service.audio.AudioService;
import com.hadenwatne.fornax.service.caching.CacheService;
import com.hadenwatne.fornax.service.types.LogType;
import com.hadenwatne.fornax.storage.BotDataStorageService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class Bot {
    private final JDA _jda;
    private final BotDataStorageService _botDataStorageService;
    private final AudioService _audioService;
    private final BotInternalService _botInternalService;
    private final CacheService _cacheService;
    private ILanguageProvider _languageProvider;
    private final List<Command> _commands;
    private String _botName;
    private String _botAvatarUrl;

    public Bot() {
        App.getLogger().Log(LogType.SYSTEM, "Bot is initializing!");

        // Instantiate
        _commands = new ArrayList<>();

        // Services
        _botDataStorageService = new BotDataStorageService();
        _botInternalService = new BotInternalService(this);
        _cacheService = new CacheService();
        _languageProvider = new DefaultLanguageProvider();

        // Start JDA
        _jda = _botInternalService.authenticate(_botDataStorageService.getBotConfiguration().botApiToken);
        startJDA();
        populateBotInfo();

        // Start audio service
        _audioService = new AudioService();

        // Commands
        registerCommands();
        _botInternalService.checkRefreshGlobalCommands(isDebugMode());
        _jda.addEventListener(new CommandListener(this));
        _jda.addEventListener(new InteractionListener(this));

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

    public final BotDataStorageService getBotDataStorageService() {
        return _botDataStorageService;
    }

    public void activateCommandOnGuild(Guild guild, Command command) {
        this._botInternalService.activateCommandOnGuild(guild, command);
    }

    public AudioService getAudioService() {
        return _audioService;
    }

    public CacheService getCacheService() {
        return _cacheService;
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

    protected abstract void registerCommands();

    protected abstract void afterInit();

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
