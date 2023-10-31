package com.hadenwatne.shmames.language;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadenwatne.fornax.App;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.service.ILanguageProvider;
import com.hadenwatne.fornax.service.types.LogType;
import com.hadenwatne.fornax.utility.FileUtility;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LanguageProvider implements ILanguageProvider {
    private final String DEFAULT_LANGUAGE_NAME = "en_default";
    private final String LANGUAGE_DIRECTORY = "languages";
    private final Shmames shmames;
    private final List<Language> languages;
    private final Gson gson;
    private final Language defaultLanguage = new Language(DEFAULT_LANGUAGE_NAME);

    // TODO prune old keys, add new ones, write the changes to disk.
    public LanguageProvider(Shmames shmames) {
        App.getLogger().Log(LogType.SYSTEM, "Loading language service...");

        this.shmames = shmames;
        gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        languages = new ArrayList<>();

        loadLanguagesFromDisk();

        App.getLogger().Log(LogType.SYSTEM, "Language loading finished!");
    }

    @Override
    public String getMessageFromKey(String messageKey, String... replacements) {
        return defaultLanguage.getMessage(messageKey, replacements);
    }

    @Override
    public String getMessageFromKey(Execution execution, String messageKey, String... replacements) {
        if(execution.isFromServer()) {
            Brain brain = shmames.getBrainController().getBrain(execution.getServer().getId());

            return getLanguageForBrain(brain).getMessage(messageKey, replacements);
        }else{
            return getMessageFromKey(messageKey, replacements);
        }
    }

    @Override
    public String getErrorFromKey(String errorKey, String... replacements) {
        return defaultLanguage.getError(errorKey, replacements);
    }

    @Override
    public String getErrorFromKey(Execution execution, String messageKey, String... replacements) {
        if(execution.isFromServer()) {
            Brain brain = shmames.getBrainController().getBrain(execution.getServer().getId());

            return getLanguageForBrain(brain).getError(messageKey, replacements);
        }else{
            return getErrorFromKey(messageKey, replacements);
        }
    }

    public Language getDefaultLanguage() {
        return this.defaultLanguage;
    }

    public Language getLanguageForBrain(Brain brain) {
        if(brain != null){
            Language l = getLanguageFromName(brain.getSettingFor(BotSettingName.SERVER_LANG).getAsString());

            if(l == null){
                return defaultLanguage;
            }

            return l;
        } else {
            return defaultLanguage;
        }
    }

    public Language getLanguageFromName(String name) {
        for (Language l : this.languages) {
            if (l.getLanguageName().equalsIgnoreCase(name)) {
                return l;
            }
        }

        return null;
    }

    private void loadLanguagesFromDisk() {
        File[] langFiles = FileUtility.ListFilesInDirectory(LANGUAGE_DIRECTORY, new JSONFileFilter());

        for(File l : langFiles) {
            Language language = this.gson.fromJson(FileUtility.LoadFileAsString(l), Language.class);

            language.setFileName(l.getName());

            if(!language.getLanguageName().equals(DEFAULT_LANGUAGE_NAME)) {
                App.getLogger().Log(LogType.SYSTEM, "\tLoaded " + language.getLanguageName());

                this.languages.add(language);
            }
        }

        this.languages.add(defaultLanguage);
    }
}
