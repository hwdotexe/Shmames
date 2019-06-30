package tech.hadenw.shmamesbot.brain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import tech.hadenw.shmamesbot.Shmames;

/**
 * Loads the global brain, then loads brains retroactively from the brain
 * directory. Provides an access point to retrieve a Guild's brain based on the
 * Guild ID. Provides a save method for an individual Guild brain. To save all
 * of them, just call this method for each.
 */
public class BrainController {
	private MotherBrain global;
	private File globalSettingsFile;
	private Gson gson;
	private List<Brain> brains;

	public BrainController() {
		gson = new Gson();
		brains = new ArrayList<Brain>();
		globalSettingsFile = new File("brains/motherBrain.json");

		// Ensure new settings are made available for the user to change.
		for(File b : discoverBrains()) {
			Brain brain = gson.fromJson(loadJSONFile(b), Brain.class);
			brains.add(brain);
			
			for(BotSetting s : Shmames.defaults) {
				boolean exists = false;
				
				for(BotSetting bs : brain.getSettings()) {
					if(bs.getName() == s.getName()) {
						exists = true;
						break;
					}
				}
				
				if(!exists) {
					brain.getSettings().add(new BotSetting(s.getName(), s.getType(), s.getValue()));
				}
			}
		}

		if (globalSettingsFile.exists()) {
			global = gson.fromJson(loadJSONFile(globalSettingsFile), MotherBrain.class);
		} else {
			global = new MotherBrain();
			global.loadDefaults();
			saveMotherBrain();
		}
	}

	public Brain getBrain(String guildID) {
		for (Brain b : brains) {
			if (b.getGuildID().equals(guildID)) {
				return b;
			}
		}

		Brain b = new Brain(guildID);
		brains.add(b);
		
		return b;
	}
	
	public MotherBrain getMotherBrain() {
		return global;
	}
	
	public List<Brain> getBrains() {
		return brains;
	}
	
	public void reloadBrain(String gid) {
		
		for(Brain b : brains) {
			if(b.getGuildID().equals(gid)) {
				brains.remove(b);
				break;
			}
		}
		
		brains.add(gson.fromJson(loadJSONFile(new File("brains/servers/"+gid+".json")), Brain.class));
	}
	
	public void saveBrain(Brain b) {
		byte[] bytes = gson.toJson(b).toString().getBytes();
		
		try {
			File bf = new File("brains/servers/"+b.getGuildID()+".json");
			FileOutputStream os = new FileOutputStream(bf);
			
			if(!bf.exists()) 
				bf.createNewFile();
			
			os.write(bytes);
			os.flush();
			os.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveMotherBrain() {
		byte[] bytes = gson.toJson(global).toString().getBytes();
		
		try {
			FileOutputStream os = new FileOutputStream(globalSettingsFile);
			
			if(!globalSettingsFile.exists()) 
				globalSettingsFile.createNewFile();
			
			os.write(bytes);
			os.flush();
			os.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String loadJSONFile(File f) {
		try {
			int data;
			FileInputStream is = new FileInputStream(f);
			String jsonData = "";

			while ((data = is.read()) != -1) {
				jsonData += (char) data;
			}

			is.close();

			return jsonData;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	private List<File> discoverBrains() {
		File dir = new File("brains/servers");

		if (!dir.exists())
			dir.mkdirs();

		File[] files = dir.listFiles();
		List<File> brains = new ArrayList<File>();

		for (File f : files) {
			if (f.isFile()) {
				if (f.getName().endsWith(".json")) {
					brains.add(f);
				}
			}
		}

		return brains;
	}
}
