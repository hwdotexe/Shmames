package tech.hadenw.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.storage.ForumWeaponObj;

public class ForumWeapon implements ICommand {
	@Override
	public String getDescription() {
		return "Engage the global meme arsenal.";
	}
	
	@Override
	public String getUsage() {
		return "fw [create|update|remove] <weapon name> [weapon link]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(((create)|(update)|(remove)) )?(\\w{3,}) ?(https?:\\/\\/.+)?$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		// Group 2 - operation ?
		// Group 6 - name
		// Group 7 - link ?
		if(m.find()) {
			String name = m.group(6).toLowerCase();
			
			if(m.group(2) != null) {
				// We want to do something
				String op = m.group(2).toLowerCase().trim();
				
				switch(op) {
					case "create":
						if(m.group(7) != null) {
							if(findFW(name) == null) {
								ForumWeaponObj nfw = new ForumWeaponObj(name, m.group(7), message.getGuild().getId());
								
								Shmames.getBrains().getMotherBrain().getForumWeapons().add(nfw);
								
								return "Created new forum weapon: **"+name+"**";
							} else {
								return "An item with that name already exists!";
							}
						} else {
							return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
						}
					case "update":
						if(m.group(7) != null) {
							ForumWeaponObj fw = findFW(name);
							
							if(fw != null) {
								if(fw.getServerID().equals(message.getGuild().getId())) {
									fw.setItemLink(m.group(7));
									
									return "Updated item with the new link!";
								}else {
									return "That item is owned by a different server!";
								}
							} else {
								return Errors.NOT_FOUND;
							}
						} else {
							return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
						}
					case "remove":
						ForumWeaponObj fw = findFW(name);
						
						if(fw != null) {
							if(fw.getServerID().equals(message.getGuild().getId())) {
								Shmames.getBrains().getMotherBrain().getForumWeapons().remove(fw);
								
								return "Weapon destroyed.";
							}else {
								return "That item is owned by a different server!";
							}
						} else {
							return Errors.NOT_FOUND;
						}
					default:
						return null;
				}
			} else {
				// Try to send the weapon
				ForumWeaponObj fw = findFW(name);
				
				if(fw != null) {
					fw.IncreaseUse();
					
					return fw.getItemLink();
				}else {
					// Couldn't find one
					return Errors.NOT_FOUND;
				}
			}
		}else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"fw", "forumweapon", "link"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
	
	private ForumWeaponObj findFW(String name) {
		for(ForumWeaponObj fw : Shmames.getBrains().getMotherBrain().getForumWeapons()) {
			if(fw.getItemName().equals(name)) {
				return fw;
			}
		}
		
		return null;
	}
}