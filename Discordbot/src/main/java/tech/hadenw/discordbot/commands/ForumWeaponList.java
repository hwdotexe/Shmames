package tech.hadenw.discordbot.commands;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.Utils;
import tech.hadenw.discordbot.storage.Brain;
import tech.hadenw.discordbot.storage.Family;
import tech.hadenw.discordbot.storage.ForumWeaponObj;

public class ForumWeaponList implements ICommand {
	@Override
	public String getDescription() {
		return "List all the Forum Weapons available to this server.";
	}
	
	@Override
	public String getUsage() {
		return "fwlist [all|search <query>]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((all)|(search ([a-z]{3,})))?$", Pattern.CASE_INSENSITIVE).matcher(args);
		Guild thisG = message.getGuild();

		if(m.find()) {
			if (m.group(2) != null) {
				// All
				StringBuilder sb = new StringBuilder();

				sb.append(buildServerFWList(thisG));

				for (String fID : Shmames.getBrains().getBrain(message.getGuild().getId()).getFamilies()) {
					Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fID);

					if (f != null) {
						for (long gid : f.getMemberGuilds()) {
							if (gid != thisG.getIdLong()) {
								Guild g = Shmames.getJDA().getGuildById(gid);

								if (g != null) {
									sb.append("\n\n");
									sb.append(buildServerFWList(g));
								}
							}
						}
					}
				}

				return sb.toString();
			} else if (m.group(3) != null) {
				// Searching for a name (g4)
				String name = m.group(4);

				StringBuilder sb = new StringBuilder();

				sb.append(searchServerList(thisG, name));

				for (String fID : Shmames.getBrains().getBrain(message.getGuild().getId()).getFamilies()) {
					Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fID);

					if (f != null) {
						for (long gid : f.getMemberGuilds()) {
							if (gid != thisG.getIdLong()) {
								Guild g = Shmames.getJDA().getGuildById(gid);

								if (g != null) {
									sb.append("\n\n");
									sb.append(searchServerList(g, name));
								}
							}
						}
					}
				}

				return sb.toString();
			} else {
				// Just list out this server
				return buildServerFWList(thisG);
			}
		}else{
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
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

	private String buildServerFWList(Guild g){
		String id = g.getId();
		HashMap<String, Integer> fwList = new HashMap<String, Integer>();

		for(ForumWeaponObj fws : Shmames.getBrains().getBrain(id).getForumWeapons()) {
			fwList.put(fws.getItemName(), fws.getUses());
		}

		LinkedHashMap<String, Integer> fwSorted = Utils.sortHashMap(fwList);
		String list = Utils.GenerateList(fwSorted, -1);

		return "**"+g.getName()+"**\n" + (list.length()>2 ? list.substring(2) : "> None Found");
	}

	private String searchServerList(Guild g, String q){
		String id = g.getId();
		HashMap<String, Integer> fwList = new HashMap<String, Integer>();

		for(ForumWeaponObj fws : Shmames.getBrains().getBrain(id).getForumWeapons()) {
			if(fws.getItemName().contains(q.toLowerCase()))
				fwList.put(fws.getItemName(), fws.getUses());
		}

		LinkedHashMap<String, Integer> fwSorted = Utils.sortHashMap(fwList);
		String list = Utils.GenerateList(fwSorted, -1);

		return "**"+g.getName()+"**\n" + (list.length()>2 ? list.substring(2) : "> No Results");
	}
}
