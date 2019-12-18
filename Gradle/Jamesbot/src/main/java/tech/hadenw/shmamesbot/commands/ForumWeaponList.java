package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
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
		
		for(ForumWeaponObj fws : Shmames.getBrains().getMotherBrain().getForumWeapons()) {
			if(fws.getServerID().equals(id)) {
				if(items.length() > 0)
					items += ", ";
				
				items += fws.getItemName();
			}
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
