package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.BrainController;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.BotSettingType;
import com.hadenwatne.shmames.models.data.BotSetting;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.MotherBrain;

import java.util.ArrayList;
import java.util.List;

public class StorageService {
    private final BrainController brainController;
    private final List<BotSetting> defaultBotSettings;

    /**
     * Initialize the service.
     */
    public StorageService() {
        this.brainController = new BrainController();
        this.defaultBotSettings = this.createDefaultSettings();
    }

    /**
     * Retrieves the BrainController object for managing storage.
     * @return The BrainController.
     */
    public BrainController getBrainController() {
        return this.brainController;
    }

    /**
     * Retrieves the bot's global settings file.
     * @return A MotherBrain object.
     */
    public MotherBrain getMotherBrain() {
        return this.brainController.getMotherBrain();
    }

    /**
     * Retrieves a server's Brain object.
     * @return A Brain object.
     */
    public Brain getBrain(String guildID) {
        return this.brainController.getBrain(guildID);
    }

    /**
     * Retrieves a list of default bot settings and their values.
     * @return A list of settings.
     */
    public List<BotSetting> getDefaultSettings() {
        return this.defaultBotSettings;
    }

    /**
     * Creates an array of currently-accepted settings per-server. Items not in this list are removed from
     * settings files when loaded. New items are added with their default values.
     */
    private List<BotSetting> createDefaultSettings() {
        List<BotSetting> settings = new ArrayList<>();
        settings.add(new BotSetting(BotSettingName.ALLOW_MODIFY, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.APPROVAL_EMOTE, BotSettingType.EMOTE, "notset"));
        settings.add(new BotSetting(BotSettingName.APPROVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
        settings.add(new BotSetting(BotSettingName.MANAGE_MUSIC, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.POLL_CLOSE, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.POLL_CREATE, BotSettingType.ROLE, "everyone"));
        settings.add(new BotSetting(BotSettingName.POLL_PIN, BotSettingType.BOOLEAN, "false"));
        settings.add(new BotSetting(BotSettingName.POLL_PIN_CHANNEL, BotSettingType.CHANNEL, "general"));
        settings.add(new BotSetting(BotSettingName.PRUNE_FW, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.REMOVAL_EMOTE, BotSettingType.EMOTE, "notset"));
        settings.add(new BotSetting(BotSettingName.REMOVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
        settings.add(new BotSetting(BotSettingName.RESET_EMOTE_STATS, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.SERVER_LANG, BotSettingType.TEXT, "default"));
        settings.add(new BotSetting(BotSettingName.TALLY_REACTIONS, BotSettingType.BOOLEAN, "true"));

        return settings;
    }
}
