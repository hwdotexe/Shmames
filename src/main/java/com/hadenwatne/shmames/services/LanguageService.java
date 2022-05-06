package com.hadenwatne.shmames.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.JSONFileFilter;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LanguageService {
	private final Gson gson;
	private final List<Lang> langs;

	private final String LANG_DIRECTORY = "langs";
	private final String DEFAULT_LANGUAGE = "en_default";

	public LanguageService() {
		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		langs = new ArrayList<>();

		// Always save a new copy of the default language file to disk.
		Lang defaultLanguage = new Lang(DEFAULT_LANGUAGE);
		FileService.SaveBytesToFile(LANG_DIRECTORY, defaultLanguage.getLangName() + ".json", gson.toJson(defaultLanguage).getBytes());

		// Load language files.
		loadLangs();
	}

	/**
	 * Gets the default language file.
	 * @return The default language.
	 */
	public Lang getDefaultLang() {
		return getLang(DEFAULT_LANGUAGE);
	}

	/**
	 * Gets the Language file for a server.
	 * @return The server's desired Locale, or default if none.
	 */
	public Lang getLangFor(@Nullable Brain brain) {
		if(brain != null){
			Lang l = getLang(brain.getSettingFor(BotSettingName.SERVER_LANG).getAsString());

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
	public Lang getLangFor(Guild guild) {
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
	public Lang getLang(String name) {
		for (Lang l : this.langs) {
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
	public List<Lang> getAllLangs() {
		return langs;
	}

	private void loadLangs() {
		File[] langFiles = FileService.ListFilesInDirectory(LANG_DIRECTORY, new JSONFileFilter());

		for(File l : langFiles) {
			Lang lang = this.gson.fromJson(FileService.LoadFileAsString(l), Lang.class);

			this.langs.add(lang);
		}
	}
}
