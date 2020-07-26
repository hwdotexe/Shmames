package tech.hadenw.discordbot.commands;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

public class ForumWeapon implements ICommand {
	@Override
	public String getDescription() {
		return "Engage the meme arsenal. Create shorthand names for your favorite GIFs, " +
				"videos, links, and more. Then, use them in chat whenever you need!";
	}
	
	@Override
	public String getUsage() {
		return "fw [create|update|remove|list|search] [weapon name] [weapon link]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^((create)|(update)|(remove)|(list)|(search)|(\\w{3,})) ?(\\w{3,})? ?(https?:\\/\\/.+)?$", Pattern.CASE_INSENSITIVE).matcher(args);

		if(m.find()) {
			String nameOrOp = m.group(1).toLowerCase();
			String optFWName = m.group(8) != null ?  m.group(8).toLowerCase() : null;
			String optURL = m.group(9) != null ?  m.group(9).toLowerCase() : null;

			switch(nameOrOp) {
				case "create":
					if(getFWCount(message.getGuild().getId()) < 100) {
						if (optFWName != null) {
							if (findFW(optFWName, message.getGuild().getId()) == null) {
								ForumWeaponObj nfw = new ForumWeaponObj(optFWName, m.group(9), message.getGuild().getId());

								Shmames.getBrains().getBrain(message.getGuild().getId()).getForumWeapons().add(nfw);

								return "Created new forum weapon: **" + optFWName + "**";
							} else {
								return "An item with that name already exists!";
							}
						} else {
							return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
						}
					}else{
						return "Sorry! I can only keep up to 100 weapons. Please remove some existing weapons before creating more.";
					}
				case "update":
					if(optFWName != null && optURL != null) {
						ForumWeaponObj fwu = findFW(optFWName, message.getGuild().getId());

						if(fwu != null) {
							if(fwu.getServerID().equals(message.getGuild().getId())) {
								fwu.setItemLink(optURL);

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
					if(optFWName != null) {
						ForumWeaponObj fwr = findFW(optFWName, message.getGuild().getId());

						if (fwr != null) {
							if (fwr.getServerID().equals(message.getGuild().getId())) {
								Shmames.getBrains().getBrain(message.getGuild().getId()).getForumWeapons().remove(fwr);

								return "Weapon destroyed.";
							} else {
								return "That item is owned by a different server!";
							}
						} else {
							return Errors.NOT_FOUND;
						}
					}else {
						return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
					}
				case "list":
					Guild thisGl = message.getGuild();

					if(optFWName != null && optFWName.equals("all")){
						// List the Family
						StringBuilder sb = new StringBuilder();

						sb.append(buildServerFWList(thisGl));

						for (String fID : Shmames.getBrains().getBrain(message.getGuild().getId()).getFamilies()) {
							Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fID);

							if (f != null) {
								for (long gid : f.getMemberGuilds()) {
									if (gid != thisGl.getIdLong()) {
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
					}else{
						// Just list this Guild
						return this.buildServerFWList(thisGl);
					}
				case "search":
					if(optFWName != null) {
						Guild thisGs = message.getGuild();
						StringBuilder sb = new StringBuilder();

						sb.append(searchServerFWList(thisGs, optFWName));

						for (String fID : Shmames.getBrains().getBrain(message.getGuild().getId()).getFamilies()) {
							Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fID);

							if (f != null) {
								for (long gid : f.getMemberGuilds()) {
									if (gid != thisGs.getIdLong()) {
										Guild g = Shmames.getJDA().getGuildById(gid);

										if (g != null) {
											sb.append("\n\n");
											sb.append(searchServerFWList(g, optFWName));
										}
									}
								}
							}
						}

						return sb.toString();
					}else{
						return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
					}
				default:
					// Try to send the weapon
					ForumWeaponObj fws = findFW(nameOrOp, message.getGuild().getId());

					if(fws != null) {
						fws.IncreaseUse();

						return fws.getItemLink();
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
	
	private ForumWeaponObj findFW(String name, String guildID) {
		if(name.equals("create") || name.equals("update") || name.equals("remove") || name.equals("list") || name.equals("search")){
			return null;
		}

		// Check local server.
		for(ForumWeaponObj fw : Shmames.getBrains().getBrain(guildID).getForumWeapons()) {
			if(fw.getItemName().equals(name)) {
				return fw;
			}
		}

		// Check other Family servers.
		for(String fid : Shmames.getBrains().getBrain(guildID).getFamilies()){
			Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fid);

			if(f != null){
				for(long gid : f.getMemberGuilds()){
					if(!Long.toString(gid).equals(guildID)){
						Brain b = Shmames.getBrains().getBrain(Long.toString(gid));

						for(ForumWeaponObj fw : b.getForumWeapons()) {
							if(fw.getItemName().equals(name)) {
								return fw;
							}
						}
					}
				}
			}
		}
		
		return null;
	}

	private int getFWCount(String guildID) {
		return Shmames.getBrains().getBrain(guildID).getForumWeapons().size();
	}

	private String buildServerFWList(Guild g) {
		String id = g.getId();
		HashMap<String, Integer> fwList = new HashMap<String, Integer>();

		for(ForumWeaponObj fws : Shmames.getBrains().getBrain(id).getForumWeapons()) {
			fwList.put(fws.getItemName(), fws.getUses());
		}

		LinkedHashMap<String, Integer> fwSorted = Utils.sortHashMap(fwList);
		String list = Utils.GenerateList(fwSorted, -1);

		return "**"+g.getName()+"**\n" + (list.length()>2 ? list.substring(2) : "> None Found");
	}

	private String searchServerFWList(Guild g, String q) {
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
