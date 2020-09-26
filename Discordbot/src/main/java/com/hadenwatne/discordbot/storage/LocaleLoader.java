package com.hadenwatne.discordbot.storage;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LocaleLoader {
	private Gson gson;
	private List<Locale> locales;

	public LocaleLoader() {
		gson = new Gson();
		locales = new ArrayList<Locale>();
	}

	public void loadLocales() {
		File folder = new File("locales");

		createDirectory(folder);

		// Always save default in case of new changes.
		saveLocale(new Locale("default"));

		// Discover locale files and add to system.
		for(File l : discoverLocales(folder)) {
			Locale locale = gson.fromJson(loadJSONFile(l), Locale.class);

			locales.add(locale);
		}
	}

	public Locale getLocale(String name) {
		for (Locale l : locales) {
			if (l.getLocaleName().equalsIgnoreCase(name)) {
				return l;
			}
		}
		
		return null;
	}

	public List<Locale> getAllLocales() {
		return locales;
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
			e.printStackTrace();
		}

		return "";
	}

	private List<File> discoverLocales(File dir) {
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

	private void saveLocale(Locale l) {
		byte[] bytes = gson.toJson(l).getBytes();

		try {
			File lf = new File("locales/"+l.getLocaleName()+".json");
			FileOutputStream os = new FileOutputStream(lf);

			if(!lf.exists())
				lf.createNewFile();

			os.write(bytes);
			os.flush();
			os.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void createDirectory(File dir) {
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}
}
