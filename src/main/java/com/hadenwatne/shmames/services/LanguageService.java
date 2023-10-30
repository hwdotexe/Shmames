package com.hadenwatne.shmames.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadenwatne.fornax.service.LoggingService;
import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.JSONFileFilter;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.fornax.service.types.LogType;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.models.data.LanguageError;
import com.hadenwatne.shmames.models.data.LanguageMessage;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LanguageService {
	public static final String DEFAULT_LANGUAGE = "en_default";

	private final Gson gson;
	private final List<Language> languages;
	private final String LANG_DIRECTORY = "langs";
	private final Language defaultLanguage = new Language(DEFAULT_LANGUAGE);

	public LanguageService() {
		LoggingService.Log(LogType.SYSTEM, "Loading Language packs...");

		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		languages = new ArrayList<>();

		// Load language files.
		loadLangs();

		// Make sure the language is up to date.
		pruneOldLangKeys();
		addNewLangKeys();

		// Update lang files on disk with changes.
		for(Language language : languages) {
			FileService.SaveBytesToFile(LANG_DIRECTORY, language.getFileName(), gson.toJson(language).getBytes());
		}

		LoggingService.Log(LogType.SYSTEM, "Language loading complete!");
	}

	/**
	 * Gets the default language file.
	 * @return The default language.
	 */
	public Language getDefaultLang() {
		return this.defaultLanguage;
	}

	/**
	 * Gets the Language file for a server.
	 * @return The server's desired Locale, or default if none.
	 */
	public Language getLangFor(@Nullable Brain brain) {
		if(brain != null){
			Language l = getLang(brain.getSettingFor(BotSettingName.SERVER_LANG).getAsString());

			if(l == null){
				return getDefaultLang();
			}

			return l;
		} else {
			return getDefaultLang();
		}
	}

	/**
	 * Gets the Lang for a server.
	 * @return The server's desired Lang, or default if none.
	 */
	public Language getLangFor(Guild guild) {
		if(guild != null){
			return getLangFor(App.Shmames.getStorageService().getBrain(guild.getId()));
		}

		return getDefaultLang();
	}

	/**
	 * Retrieves the Language file with the given name, or null if none exist.
	 * @param name The name of the Language file.
	 * @return The Language file, or null if none found.
	 */
	public Language getLang(String name) {
		for (Language l : this.languages) {
			if (l.getLangName().equalsIgnoreCase(name)) {
				return l;
			}
		}

		return null;
	}

	/**
	 * Returns a list of all Language files currently loaded.
	 * @return A list of Language files.
	 */
	public List<Language> getAllLangs() {
		return languages;
	}

	private void loadLangs() {
		File[] langFiles = FileService.ListFilesInDirectory(LANG_DIRECTORY, new JSONFileFilter());

		for(File l : langFiles) {
			Language language = this.gson.fromJson(FileService.LoadFileAsString(l), Language.class);

			language.setFileName(l.getName());

			if(!language.getLangName().equals(DEFAULT_LANGUAGE)) {
				LoggingService.Log(LogType.SYSTEM, "\tLoaded " + language.getLangName());

				this.languages.add(language);
			}
		}

		this.languages.add(defaultLanguage);
	}

	private void pruneOldLangKeys() {
		for(Language language : languages) {
			language.messages.removeIf(message -> message.getKey() == null);
			language.errors.removeIf(error -> error.getKey() == null);
		}
	}

	private void addNewLangKeys() {
		for(Language language : languages) {
			for(LanguageKeys key : LanguageKeys.values()) {
				if(language.getMsg(key) == null) {
					LanguageMessage message = defaultLanguage.getLanguageMessage(key);

					language.messages.add(new LanguageMessage(key, message.getValues()));
				}
			}

			for(ErrorKeys key : ErrorKeys.values()) {
				if(language.getError(key) == null) {
					LanguageError message = defaultLanguage.getLanguageError(key);

					language.errors.add(new LanguageError(key, message.getValues()));
				}
			}

			Collections.sort(language.messages);
			Collections.sort(language.errors);
		}
	}
}
