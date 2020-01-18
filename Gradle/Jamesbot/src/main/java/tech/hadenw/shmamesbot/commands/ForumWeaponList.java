package tech.hadenw.shmamesbot.commands;

import java.util.HashMap;
import java.util.LinkedHashMap;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.Utils;
import tech.hadenw.shmamesbot.brain.ForumWeaponObj;

public class ForumWeaponList implements ICommand {
	@Override
	public String getDescription() {
		return "List all the forum weapons on this server.";
	}
	
	@Override
	public String getUsage() {
		return "fwlist";
	}

	@Override
	public String run(String args, User author, Message message) {
		String items = "";
		String id = message.getGuild().getId();
		
		// Create list
		HashMap<String, Integer> fwList = new HashMap<String, Integer>();
		
		for(ForumWeaponObj fws : Shmames.getBrains().getMotherBrain().getForumWeapons()) {
			if(fws.getServerID().equals(id)) {
				fwList.put(fws.getItemName(), fws.getUses());
			}
		}
		
		// Sort
		LinkedHashMap<String, Integer> fwSorted = Utils.sortHashMap(fwList);
		
		for(String c : fwSorted.keySet()) {
			if(items.length() > 0)
				items += "\n";
			items += "`"+c+"`: "+fwSorted.get(c);
		}
		
		return "**This arsenal contains the following weapons:**\n"+items;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"fwlist", "fwarsenal"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
