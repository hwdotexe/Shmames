package com.hadenwatne.shmames.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.ShmamesLogger;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Family;
import com.hadenwatne.shmames.models.ForumWeaponObj;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;

import javax.annotation.Nullable;

public class ForumWeapon implements ICommand {
	private final CommandStructure commandStructure;

	public ForumWeapon() {
		this.commandStructure = CommandBuilder.Create("forumweapon")
				.addAlias("fw")
				/*
				.addParameters(
						new CommandParameter("subCommand", "The action to run.", ParameterType.SELECTION, false)
								.addSelectionOptions("create", "update", "remove", "list", "search", "alias", "prune"),
						new CommandParameter("forumWeaponName", "The name of the forum weapon.", ParameterType.STRING)
								.setPattern("\\w{3,}"),
						new CommandParameter("forumWeaponAlias", "The alternative name for this weapon.", ParameterType.STRING)
								.setPattern("\\w{3,}"),
						new CommandParameter("forumWeaponLink", "The URL this weapon directs to.", ParameterType.STRING)
								.setPattern("https?:\\/\\/[\\w\\d:/.\\-?&=%#@]+")
				)
				*/
				.addSubCommands(
						CommandBuilder.Create("create")
								.addParameters(
										new CommandParameter("createWeaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}"),
										new CommandParameter("createWeaponLink", "The URL this weapon directs to.", ParameterType.STRING)
												.setPattern("https?:\\/\\/[\\w\\d:/.\\-?&=%#@]+")
								)
								.build(),
						CommandBuilder.Create("update")
								.addParameters(
										new CommandParameter("updateWeaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}"),
										new CommandParameter("updateWeaponLink", "The URL this weapon directs to.", ParameterType.STRING)
												.setPattern("https?:\\/\\/[\\w\\d:/.\\-?&=%#@]+")
								)
								.build(),
						CommandBuilder.Create("remove")
								.addParameters(
										new CommandParameter("removeWeaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}")
								)
								.build(),
						CommandBuilder.Create("list")
								.build(),
						CommandBuilder.Create("search")
								.addParameters(
										new CommandParameter("partialWeaponName", "The partial name of the Forum Weapon to search for.", ParameterType.STRING)
												.setPattern("\\w{3,}")
								)
								.build(),
						CommandBuilder.Create("alias")
								.addParameters(
										new CommandParameter("aliasWeaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}"),
										new CommandParameter("newAlias", "The URL this weapon directs to.", ParameterType.STRING)
												.setPattern("\\w{3,}")
								)
								.build(),
						CommandBuilder.Create("prune")
								.build()
				)
				.build();
	}

	@Override
	public String getDescription() {
		return "Create shorthand names for your favorite links, and share them in a snap.";
	}
	
	@Override
//	public String getUsage() {
//		return "fw [create|update|remove|list|search|alias|prune] [weapon name] [weapon alias] [weapon link]";
//	}
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`fw dekuheadbang`\n" +
				"`fw create dekuheadbang http://link.to.meme`\n" +
				"`fw update dekuheadbang http://new.link.to.meme`\n" +
				"`fw remove dekuheadbang`\n" +
				"`fw list`\n" +
				"`fw list all`\n" +
				"`fw search deku`\n" +
				"`fw alias dekuheadbang dekunod`\n" +
				"`fw prune`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		String nameOrOp = m.group(1).toLowerCase();
		String optFWName = m.group(11) != null ? m.group(11).toLowerCase() : null;
		String optFWAlias = m.group(12) != null ? m.group(12).toLowerCase() : null;
		String optURL = m.group(13);

		switch (nameOrOp) {
			case "create":
				cmdCreate();

				if (optURL != null) {

				} else {
					return lang.wrongUsage(getUsage());
				}
			case "update":
				if (optFWName != null && optURL != null) {
					ForumWeaponObj fwu = findFW(optFWName, message.getGuild().getId());

					if (fwu != null) {
						if (fwu.getServerID().equals(message.getGuild().getId())) {
							fwu.setItemLink(optURL);

							return lang.getMsg(Langs.FORUM_WEAPON_UPDATED);
						} else {
							return lang.getError(Errors.FORUM_WEAPON_OWNED_OTHER, true);
						}
					} else {
						return lang.getError(Errors.NOT_FOUND, true);
					}
				} else {
					return lang.wrongUsage(getUsage());
				}
			case "destroy":
			case "remove":
				if (optFWName != null) {
					ForumWeaponObj fwr = findFW(optFWName, message.getGuild().getId());

					if (fwr != null) {
						if (fwr.getServerID().equals(message.getGuild().getId())) {
							Shmames.getBrains().getBrain(message.getGuild().getId()).getForumWeapons().remove(fwr);

							return lang.getMsg(Langs.FORUM_WEAPON_DESTROYED);
						} else {
							return lang.getError(Errors.FORUM_WEAPON_OWNED_OTHER, true);
						}
					} else {
						return lang.getError(Errors.NOT_FOUND, true);
					}
				} else {
					return lang.wrongUsage(getUsage());
				}
			case "list":
				Guild thisGl = message.getGuild();

				if (optFWName != null && optFWName.equals("all")) {
					// List the Family
					StringBuilder sb = new StringBuilder();

					sb.append(buildServerFWList(thisGl));

					for (String fID : brain.getFamilies()) {
						Family f = Shmames.getBrains().getMotherBrain().getFamilyByID(fID);

						if (f != null) {
							for (long gid : f.getMemberGuilds()) {
								if (gid != thisGl.getIdLong()) {
									Guild g = Shmames.getJDA().getGuildById(gid);

									if (g != null) {
										sb.append(System.lineSeparator());
										sb.append(System.lineSeparator());
										sb.append(buildServerFWList(g));
									}
								}
							}
						}
					}

					return sb.toString();
				} else {
					// Just list this Guild
					return this.buildServerFWList(thisGl);
				}
			case "search":
				if (optFWName != null) {
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
				} else {
					return lang.wrongUsage(getUsage());
				}
			case "alias":
				if (optFWName != null && optFWAlias != null) {
					ForumWeaponObj fwa = findFW(optFWName, message.getGuild().getId());

					if (fwa != null) {
						if (!fwa.getAliases().contains(optFWAlias)) {
							ForumWeaponObj fwother = findFW(optFWAlias, message.getGuild().getId());

							if (fwother == null) {
								fwa.getAliases().add(optFWAlias);

								return lang.getMsg(Langs.FORUM_WEAPON_ADDED_ALIAS);
							} else {
								return lang.getError(Errors.ALREADY_EXISTS, true);
							}
						} else {
							return lang.getError(Errors.ALREADY_EXISTS, true);
						}
					} else {
						return lang.getError(Errors.NOT_FOUND, true);
					}
				} else {
					return lang.wrongUsage(getUsage());
				}
			case "prune":
				if (Utils.checkUserPermission(brain.getSettingFor(BotSettingName.PRUNE_FW), message.getMember())) {
					List<ForumWeaponObj> unused = getServerUnusedFWs();

					sendPrunedFWs(message.getGuild().getName(), message.getTextChannel(), unused);

					for (ForumWeaponObj fw : unused) {
						this.brain.getForumWeapons().remove(fw);
					}

					return lang.getMsg(Langs.FORUM_WEAPONS_PRUNED, new String[]{Integer.toString(unused.size())});
				} else {
					return lang.getError(Errors.NO_PERMISSION_USER, true);
				}
			default:
				// Try to send the weapon
				ForumWeaponObj fws = findFW(nameOrOp, message.getGuild().getId());

				if (fws != null) {
					fws.IncreaseUse();

					return fws.getItemLink();
				} else {
					// Couldn't find one
					return lang.getError(Errors.NOT_FOUND, true);
				}
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}

	private String cmdCreate(Lang lang, Brain brain) {
		String time = args.getAsString("time");

		if (getFWCount(message.getGuild().getId()) < 100) {
			if (optFWName != null) {
				if (optFWName.equals("create") || optFWName.equals("update") || optFWName.equals("remove") || optFWName.equals("list") || optFWName.equals("search") || optFWName.equals("prune")) {
					return lang.getError(Errors.RESERVED_WORD, true);
				}

				if (findFW(optFWName, message.getGuild().getId()) == null) {
					ForumWeaponObj nfw = new ForumWeaponObj(optFWName, optURL, message.getGuild().getId());
					ForumWeaponObj existingUrl = findFWByURL(optURL, message.getGuild().getId());

					brain.getForumWeapons().add(nfw);

					return lang.getMsg(Langs.FORUM_WEAPON_CREATED, new String[]{optFWName})
							+ (existingUrl != null
							? "\n> " + lang.getMsg(Langs.FORUM_WEAPON_DUPLICATE, new String[]{existingUrl.getItemName()})
							: "");
				} else {
					return lang.getError(Errors.ALREADY_EXISTS, true);
				}
			} else {
				return lang.wrongUsage(getUsage());
			}
		} else {
			return lang.getError(Errors.FORUM_WEAPON_MAXIMUM_REACHED, true);
		}
	}

	private List<ForumWeaponObj> getServerUnusedFWs() {
		List<ForumWeaponObj> unused = new ArrayList<>();

		for(ForumWeaponObj obj : this.brain.getForumWeapons()) {
			if(obj.getUses() == 0) {
				unused.add(obj);
			}
		}

		return unused;
	}

	private ForumWeaponObj findFW(String name, String guildID) {
		// Check local server.
		for(ForumWeaponObj fw : Shmames.getBrains().getBrain(guildID).getForumWeapons()) {
			if(fw.getItemName().equals(name) || fw.getAliases().contains(name)) {
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
							if(fw.getItemName().equals(name) || fw.getAliases().contains(name)) {
								return fw;
							}
						}
					}
				}
			}
		}
		
		return null;
	}

	private ForumWeaponObj findFWByURL(String url, String guildID) {
		// Check local server.
		for(ForumWeaponObj fw : Shmames.getBrains().getBrain(guildID).getForumWeapons()) {
			if(fw.getItemLink().equals(url)) {
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
							if(fw.getItemLink().equals(url)) {
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
		String list = Utils.generateList(fwSorted, -1, true);

		return "**"+g.getName()+"**" + System.lineSeparator() + (list.length()>2 ? list.substring(2) : "> None Found");
	}

	private String searchServerFWList(Guild g, String q) {
		String id = g.getId();
		HashMap<String, Integer> fwList = new HashMap<String, Integer>();

		for(ForumWeaponObj fws : Shmames.getBrains().getBrain(id).getForumWeapons()) {
			if(fws.getItemName().contains(q.toLowerCase()))
				fwList.put(fws.getItemName(), fws.getUses());
		}

		LinkedHashMap<String, Integer> fwSorted = Utils.sortHashMap(fwList);
		String list = Utils.generateList(fwSorted, -1, true);

		return "**"+g.getName()+"**" + System.lineSeparator() + (list.length()>2 ? list.substring(2) : "> No Results");
	}

	private void sendPrunedFWs(String guildName, MessageChannel c, List<ForumWeaponObj> fws) {
		StringBuilder pruned = new StringBuilder("Pruned ForumWeapons\n");

		pruned.append("===================\n");
		pruned.append("= Name:\t\tURL   =\n");
		pruned.append("===================\n");

		// Build list.
		for(ForumWeaponObj fw : fws) {
			pruned.append("\n");
			pruned.append(fw.getItemName());
			pruned.append(":\t\t");
			pruned.append(fw.getItemLink());
		}

		// Save to file.
		File dir = new File("reports");
		File f = new File("reports/"+guildName+".txt");
		dir.mkdirs();

		try {
			f.createNewFile();

			FileOutputStream fo = new FileOutputStream(f);
			fo.write(pruned.toString().getBytes());
			fo.flush();
			fo.close();
		} catch (Exception e) {
			ShmamesLogger.logException(e);
		}

		try {
			c.sendFile(f).complete();

			// Delete on disk.
			f.delete();
		} catch(Exception e) {
			ShmamesLogger.logException(e);

			c.sendMessage(lang.getError(Errors.NO_PERMISSION_BOT, true)).queue();
		}
	}
}
