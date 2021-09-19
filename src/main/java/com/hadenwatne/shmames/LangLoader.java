package com.hadenwatne.shmames;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadenwatne.shmames.models.data.Lang;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LangLoader {
	private Gson gson;
	private List<Lang> langs;

	private final String LANG_DIRECTORY = "langs";

	public LangLoader() {
		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		langs = new ArrayList<Lang>();

		// Always save a new copy of the default language file.
		Lang def = new Lang("default");
		Utils.saveBytesToFile(LANG_DIRECTORY, def.getLangName() + ".json", gson.toJson(def).getBytes());

		// Load language files.
		loadLangs();
	}

	private void loadLangs() {
		File[] langFiles = Utils.listFilesInDirectory(LANG_DIRECTORY, new JSONFileFilter());

		for(File l : langFiles) {
			Lang lang = gson.fromJson(Utils.loadFileAsString(l), Lang.class);

			langs.add(lang);
		}
	}

	public Lang getLang(String name) {
		for (Lang l : langs) {
			if (l.getLangName().equalsIgnoreCase(name)) {
				return l;
			}
		}
		
		return null;
	}

	public List<Lang> getAllLangs() {
		return langs;
	}
}
