package com.hadenwatne.shmames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.hadenwatne.shmames.ShmamesLogger;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.BotSetting;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.MotherBrain;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import com.hadenwatne.shmames.models.Poll;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.tasks.JTimerTask;
import com.hadenwatne.shmames.tasks.PollTask;

/**
 * Responsible for serialization of server-specific data files ("brains").
 */
public class BrainController {
	private MotherBrain mb;
	private File mbFile;
	private Gson gson;
	private List<Brain> brains;

	public BrainController() {
		gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		brains = new ArrayList<Brain>();
		mbFile = new File("brains/motherBrain.json");
	}

	public void loadMotherBrain(){
		if (mbFile.exists()) {
			mb = gson.fromJson(loadJSONFile(mbFile), MotherBrain.class);
		} else {
			File dir = new File("brains");

			if (!dir.exists())
				dir.mkdirs();

			mb = new MotherBrain();
			mb.loadDefaults();
			saveMotherBrain();
		}
	}

	public void loadServerBrains(){
		// Load server settings files.
		for(File b : discoverBrains()) {
			Brain brain = gson.fromJson(loadJSONFile(b), Brain.class);

			//If this brain belongs to a deleted server, remove it and continue.
			if(Shmames.getJDA().getGuildById(brain.getGuildID()) != null) {
				brains.add(brain);
			} else {
				b.delete();
				continue;
			}

			// Activate any threads that this brain may have had.
			if(brain.getActivePolls().size() > 0) {
				for(Poll p : brain.getActivePolls()) {
					// Create new task
					Timer t = new Timer();
					TextChannel ch = Shmames.getJDA().getGuildById(brain.getGuildID()).getTextChannelById(p.getChannelID());
					Message m = ch.retrieveMessageById(p.getMessageID()).complete();

					if(m != null) {
						t.schedule(new PollTask(p, m), p.getExpiration());
					}
				}
			}

			if(brain.getTimers().size() > 0){
				for(JTimerTask t : brain.getTimers()){
					t.rescheduleTimer();
				}
			}

			// Ensure new settings are made available for the user to change.
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

			// Remove any settings that are no longer supported.
			for(BotSetting bs : new ArrayList<BotSetting>(brain.getSettings())) {
				boolean contains = false;

				for(BotSettingName s : BotSettingName.values()) {
					if(bs.getName()==s) {
						contains = true;
						break;
					}
				}

				if(!contains)
					brain.getSettings().remove(bs);
			}

			// Manually reset any cooldowns that don't have a task set up.
			if(brain.getReportCooldown())
				brain.setReportCooldown(false);

			if(brain.getJinping())
				brain.setJinping(false);
		}
	}

	/**
	 * Retrieves the settings file for a particular server.
	 * @param guildID The server to pull up.
	 * @return The server's settings file.
	 */
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
	
	/**
	 * Retrieves the global settings file for the bot.
	 * @return The global settings file.
	 */
	public MotherBrain getMotherBrain() {
		return mb;
	}
	
	/**
	 * Retrieves a list of server settings files currently loaded.
	 * @return A list of loaded settings files.
	 */
	public List<Brain> getBrains() {
		return brains;
	}
	
	/**
	 * Dumps the server's settings file from memory, and loads from file.
	 * @param gid The server to reload.
	 */
	public void reloadBrain(String gid) {
		for(Brain b : brains) {
			if(b.getGuildID().equals(gid)) {
				brains.remove(b);
				break;
			}
		}
		
		brains.add(gson.fromJson(loadJSONFile(new File("brains/servers/"+gid+".json")), Brain.class));
	}
	
	/**
	 * Saves a settings object to file.
	 * @param b The settings object.
	 */
	public void saveBrain(Brain b) {
		byte[] bytes = gson.toJson(b).getBytes();
		
		try {
			File bf = new File("brains/servers/"+b.getGuildID()+".json");
			FileOutputStream os = new FileOutputStream(bf);
			
			if(!bf.exists()) 
				bf.createNewFile();
			
			os.write(bytes);
			os.flush();
			os.close();
		}catch(Exception e) {
			ShmamesLogger.logException(e);
		}
	}
	
	/**
	 * Saves the global settings object to file.
	 */
	public void saveMotherBrain() {
		byte[] bytes = gson.toJson(mb).toString().getBytes();
		
		try {
			FileOutputStream os = new FileOutputStream(mbFile);
			
			if(!mbFile.exists())
				mbFile.createNewFile();
			
			os.write(bytes);
			os.flush();
			os.close();
		}catch(Exception e) {
			ShmamesLogger.logException(e);
		}
	}
	
	/**
	 * Loads the text contained in a file.
	 * @param f The file to load.
	 * @return A string containing the file's text.
	 */
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
	
	/**
	 * Scans the settings directory and returns a list of settings files it has found.
	 * @return A list of settings files.
	 */
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
