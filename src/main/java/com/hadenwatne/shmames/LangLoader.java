package com.hadenwatne.shmames;

import com.google.gson.Gson;
import com.hadenwatne.shmames.ShmamesLogger;
import com.hadenwatne.shmames.models.Lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LangLoader {
	private Gson gson;
	private List<Lang> langs;

	public LangLoader() {
		gson = new Gson();
		langs = new ArrayList<Lang>();
	}

	public void loadLangs() {
		File folder = new File("langs");

		createDirectory(folder);

		// Always save default in case of new changes.
		saveLang(new Lang("default"));

		// Discover lang files and add to system.
		for(File l : discoverLangs(folder)) {
			Lang lang = gson.fromJson(loadJSONFile(l), Lang.class);

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

	private String loadJSONFile(File f) {
		try {
			int data;
			FileInputStream is = new FileInputStream(f);
			StringBuilder jsonData = new StringBuilder();

			while ((data = is.read()) != -1) {
				jsonData.append((char) data);
			}

			is.close();

			return jsonData.toString();
		} catch (Exception e) {
			ShmamesLogger.logException(e);
		}

		return "";
	}

	private List<File> discoverLangs(File dir) {
		File[] files = dir.listFiles();
		List<File> locs = new ArrayList<File>();

		for (File f : files) {
			if (f.isFile()) {
				if (f.getName().endsWith(".json")) {
					locs.add(f);
				}
			}
		}

		return locs;
	}

	private void saveLang(Lang l) {
		byte[] bytes = gson.toJson(l).getBytes();

		try {
			File lf = new File("langs/"+l.getLangName()+".json");
			FileOutputStream os = new FileOutputStream(lf);

			if(!lf.exists())
				lf.createNewFile();

			os.write(bytes);
			os.flush();
			os.close();
		}catch(Exception e) {
			ShmamesLogger.logException(e);
		}
	}

	private void createDirectory(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
}
