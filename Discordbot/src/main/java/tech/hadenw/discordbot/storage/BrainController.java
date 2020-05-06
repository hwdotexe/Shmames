package tech.hadenw.discordbot.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.google.gson.Gson;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import tech.hadenw.discordbot.Poll;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.TriggerType;
import tech.hadenw.discordbot.tasks.PollTask;

/**
 * Loads the global brain, then loads brains retroactively from the brain
 * directory. Provides an access point to retrieve a Guild's brain based on the
 * Guild ID. Provides a save method for an individual Guild brain. To save all
 * of them, just call this method for each.
 */
public class BrainController {
	private MotherBrain mb;
	private File mbFile;
	private Gson gson;
	private List<Brain> brains;

	/**
	 * Performs on-restart loading operations and prepares settings files
	 * for each server attached to the bot.
	 */
	public BrainController() {
		gson = new Gson();
		brains = new ArrayList<Brain>();
		mbFile = new File("brains/motherBrain.json");

		// Load server settings files.
		for(File b : discoverBrains()) {
			Brain brain = gson.fromJson(loadJSONFile(b), Brain.class);

			//If this brain belongs to a deleted server, remove it and continue
			if(Shmames.getJDA().getGuildById(brain.getGuildID()) != null) {
				brains.add(brain);
			}
			else {
				b.delete();
				continue;
			}
			
			// Activate any threads that this brain may have had.
			// TODO this will change when we create a state.
			if(brain.getActivePolls().size() > 0) {
				for(Poll p : brain.getActivePolls()) {
					// Create new task
			        Timer t = new Timer();
			        TextChannel ch = Shmames.getJDA().getGuildById(brain.getGuildID()).getTextChannelById(p.getChannelID());
			        Message m = ch.retrieveMessageById(p.getMessageID()).complete();
			        
			        if(m != null)
			        	t.schedule(new PollTask(p, m), p.getExpiration());
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

		if (mbFile.exists()) {
			mb = gson.fromJson(loadJSONFile(mbFile), MotherBrain.class);
		} else {
			mb = new MotherBrain();
			mb.loadDefaults();
			saveMotherBrain();
		}

		// TODO temporary migration of ForumWeapons
		for(ForumWeaponObj o : new ArrayList<ForumWeaponObj>(mb.getForumWeapons())){
			Brain b = getBrain(o.getServerID());

			b.getForumWeapons().add(o);
			mb.getForumWeapons().remove(o);

			System.out.println("Moved FW \""+o.getItemName()+"\" to a brain file! ("+b.getForumWeapons().size()+")");
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
