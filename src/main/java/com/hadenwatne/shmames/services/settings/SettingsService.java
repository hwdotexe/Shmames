package com.hadenwatne.shmames.services.settings;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;
import com.hadenwatne.shmames.services.settings.types.BotSettingType;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class SettingsService {
    private final Shmames shmames;
    private final List<BotSetting> defaultSettings;

    public SettingsService(Shmames shmames) {
        this.shmames = shmames;
        this.defaultSettings = createDefaultSettings();
    }

    public void validateBrainSettings(Brain brain) {
        // Ensure new settings are made available for the user to change.
        for (BotSetting defaultSetting : defaultSettings) {
            boolean exists = false;

            for (BotSetting botSetting : brain.getSettings()) {
                if (botSetting.getName() == defaultSetting.getName()) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                BotSetting newSetting = new BotSetting(defaultSetting.getName(), defaultSetting.getType(), defaultSetting.getAsString());

                // Before adding a new ROLE setting with the "everyone" default, set its ID to this server's public role.
                if (newSetting.getType() == BotSettingType.ROLE && newSetting.getAsString().equalsIgnoreCase("everyone")) {
                    Role everyone = shmames.getJDA().getGuildById(brain.getGuildID()).getPublicRole();

                    newSetting.setValue(shmames, everyone.getId(), brain);
                }

                brain.getSettings().add(newSetting);
            }
        }

        // Remove any settings that are no longer supported.
        for (BotSetting bs : new ArrayList<>(brain.getSettings())) {
            boolean contains = false;

            for (BotSettingName s : BotSettingName.values()) {
                if (bs.getName() == s) {
                    contains = true;
                    break;
                }
            }

            if (!contains)
                brain.getSettings().remove(bs);
        }
    }

    public List<BotSetting> getDefaultSettings() {
        return defaultSettings;
    }

    private List<BotSetting> createDefaultSettings() {
        List<BotSetting> settings = new ArrayList<>();
        settings.add(new BotSetting(BotSettingName.ALLOW_MODIFY, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.ALLOW_PIN, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.APPROVAL_EMOTE, BotSettingType.EMOTE, "notset"));
        settings.add(new BotSetting(BotSettingName.APPROVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
        settings.add(new BotSetting(BotSettingName.MANAGE_GACHA, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.MANAGE_MUSIC, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.PIN_CHANNEL, BotSettingType.CHANNEL, "general"));
        settings.add(new BotSetting(BotSettingName.POLL_CLOSE, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.POLL_CREATE, BotSettingType.ROLE, "everyone"));
        settings.add(new BotSetting(BotSettingName.PRUNE_FW, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.RESET_TALLIES, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.REMOVAL_EMOTE, BotSettingType.EMOTE, "notset"));
        settings.add(new BotSetting(BotSettingName.REMOVAL_THRESHOLD, BotSettingType.NUMBER, "3"));
        settings.add(new BotSetting(BotSettingName.RESET_EMOTE_STATS, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.ROLES_CONFIGURE, BotSettingType.ROLE, "administrator"));
        settings.add(new BotSetting(BotSettingName.SERVER_LANG, BotSettingType.TEXT, shmames.getLanguageProvider().getDefaultLanguage().getLanguageName()));
        settings.add(new BotSetting(BotSettingName.TALLY_REACTIONS, BotSettingType.BOOLEAN, "true"));

        return settings;
    }
}