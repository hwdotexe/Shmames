package com.hadenwatne.shmames.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import com.hadenwatne.shmames.ShmamesLogger;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.ForumWeaponObj;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.command.ShmamesSubCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.Guild;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;


public class ForumWeapon implements ICommand {
	private final CommandStructure commandStructure;

	public ForumWeapon() {
		this.commandStructure = CommandBuilder.Create("forumweapon", "Create shorthand names for your favorite links, and share them in a snap.")
				.addAlias("fw")
				.addSubCommands(
						CommandBuilder.Create("create", "Create a new Forum Weapon.")
								.addParameters(
										new CommandParameter("createWeaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}"),
										new CommandParameter("createWeaponLink", "The URL this weapon directs to.", ParameterType.STRING)
												.setPattern("https?:\\/\\/[\\w\\d:/.\\-?&=%#@]+")
								)
								.build(),
						CommandBuilder.Create("update", "Change the URL of an existing Forum Weapon.")
								.addParameters(
										new CommandParameter("updateWeaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}"),
										new CommandParameter("updateWeaponLink", "The URL this weapon directs to.", ParameterType.STRING)
												.setPattern("https?:\\/\\/[\\w\\d:/.\\-?&=%#@]+")
								)
								.build(),
						CommandBuilder.Create("remove", "Delete a Forum Weapon.")
								.addParameters(
										new CommandParameter("removeWeaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}")
								)
								.build(),
						CommandBuilder.Create("list", "List all available Forum Weapons.")
								.addParameters(
										new CommandParameter("all", "Whether to search all connected guilds.", ParameterType.BOOLEAN, false)
								)
								.build(),
						CommandBuilder.Create("search", "Search for a Forum Weapon.")
								.addParameters(
										new CommandParameter("searchTerm", "The partial name of the Forum Weapon to search for.", ParameterType.STRING)
												.setPattern("\\w{3,}")
								)
								.build(),
						CommandBuilder.Create("alias", "Add an alternative name for a Forum Weapon.")
								.addParameters(
										new CommandParameter("aliasWeaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}"),
										new CommandParameter("newAlias", "The URL this weapon directs to.", ParameterType.STRING)
												.setPattern("\\w{3,}")
								)
								.build(),
						CommandBuilder.Create("send", "Send a Forum Weapon.")
								.addParameters(
										new CommandParameter("weaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}")
								)
								.build(),
						CommandBuilder.Create("prune", "List and delete unused Forum Weapons.")
								.build()
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
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
		ShmamesSubCommandData subCommand = data.getSubCommand();

		switch (subCommand.getCommandName().toLowerCase()) {
			case "create":
				return cmdCreate(lang, brain, data.getServer(), subCommand.getArguments());
			case "update":
				return cmdUpdate(lang, brain, data.getServer(), subCommand.getArguments());
			case "destroy":
			case "remove":
				return cmdRemove(lang, brain, data.getServer(), subCommand.getArguments());
			case "list":
				return cmdList(brain, data.getServer(), subCommand.getArguments());
			case "search":
				return cmdSearch(brain, data.getServer(), subCommand.getArguments());
			case "alias":
				return cmdAlias(lang, brain, data.getServer(), subCommand.getArguments());
			case "prune":
				if (Utils.checkUserPermission(data.getServer(), brain.getSettingFor(BotSettingName.PRUNE_FW), data.getAuthor())) {
					return cmdPrune(lang, brain, data.getServer(), data.getMessagingChannel());
				} else {
					return lang.getError(Errors.NO_PERMISSION_USER, true);
				}
			default:
				// Try to send the weapon
				String weaponName = subCommand.getArguments().getAsString("weaponName");
				ForumWeaponObj fws = findFW(weaponName, brain, data.getServer());

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

	private String cmdCreate(Lang lang, Brain brain, Guild server, ShmamesCommandArguments args) {
		String weaponName = args.getAsString("createWeaponName").toLowerCase();
		String weaponURL = args.getAsString("createWeaponLink");

		if (getFWCount(brain) < 100) {
				if (weaponName.equals("create") || weaponName.equals("update") || weaponName.equals("remove")
						|| weaponName.equals("list") || weaponName.equals("search") || weaponName.equals("prune")) {
					return lang.getError(Errors.RESERVED_WORD, true);
				}

				if (findFW(weaponName, brain, server) == null) {
					ForumWeaponObj newWeapon = new ForumWeaponObj(weaponName, weaponURL, server.getId());
					ForumWeaponObj existingUrl = findFWByURL(weaponURL, brain, server);

					brain.getForumWeapons().add(newWeapon);

					return lang.getMsg(Langs.FORUM_WEAPON_CREATED, new String[]{weaponName})
							+ (existingUrl != null
							? "\n> " + lang.getMsg(Langs.FORUM_WEAPON_DUPLICATE, new String[]{existingUrl.getItemName()})
							: "");
				} else {
					return lang.getError(Errors.ALREADY_EXISTS, true);
				}
		} else {
			return lang.getError(Errors.FORUM_WEAPON_MAXIMUM_REACHED, true);
		}
	}

	private String cmdUpdate(Lang lang, Brain brain, Guild server, ShmamesCommandArguments args) {
		String weaponName = args.getAsString("updateWeaponName").toLowerCase();
		String weaponURL = args.getAsString("updateWeaponLink");
		ForumWeaponObj forumWeapon = findFW(weaponName, brain, server);

		if (forumWeapon != null) {
			if (forumWeapon.getServerID().equals(server.getId())) {
				forumWeapon.setItemLink(weaponURL);

				return lang.getMsg(Langs.FORUM_WEAPON_UPDATED);
			} else {
				return lang.getError(Errors.FORUM_WEAPON_OWNED_OTHER, true);
			}
		} else {
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}

	private String cmdRemove(Lang lang, Brain brain, Guild server, ShmamesCommandArguments args) {
		String weaponName = args.getAsString("removeWeaponName").toLowerCase();
		ForumWeaponObj fwr = findFW(weaponName, brain, server);

		if (fwr != null) {
			if (fwr.getServerID().equals(server.getId())) {
				brain.getForumWeapons().remove(fwr);

				return lang.getMsg(Langs.FORUM_WEAPON_DESTROYED);
			} else {
				return lang.getError(Errors.FORUM_WEAPON_OWNED_OTHER, true);
			}
		} else {
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}

	private String cmdList(Brain brain, Guild server, ShmamesCommandArguments args) {
		boolean all = args.getAsBoolean("all");

		if (all) {
			// List the Family
			StringBuilder sb = new StringBuilder();

			sb.append(buildServerFWList(server));

			for(Guild family : Utils.GetConnectedFamilyGuilds(brain, server)) {
				sb.append(System.lineSeparator());
				sb.append(System.lineSeparator());
				sb.append(buildServerFWList(family));
			}

			return sb.toString();
		} else {
			// Just list this Guild
			return this.buildServerFWList(server);
		}
	}

	private String cmdSearch(Brain brain, Guild server, ShmamesCommandArguments args) {
		String searchTerm = args.getAsString("searchTerm");
		StringBuilder sb = new StringBuilder();

		// Search this server.
		sb.append(searchServerFWList(brain, server.getName(), searchTerm));

		// Search the Family.
		for(Guild family : Utils.GetConnectedFamilyGuilds(brain, server)) {
			Brain familyBrain = Shmames.getBrains().getBrain(family.getId());
			sb.append(searchServerFWList(familyBrain, family.getName(), searchTerm));
		}

		return sb.toString();
	}

	private String cmdAlias(Lang lang, Brain brain, Guild server, ShmamesCommandArguments args) {
		String weaponName = args.getAsString("aliasWeaponName").toLowerCase();
		String aliasName = args.getAsString("newAlias");
		ForumWeaponObj forumWeapon = findFW(weaponName, brain, server);

		if (forumWeapon != null) {
			if (!forumWeapon.getAliases().contains(aliasName)) {
				ForumWeaponObj otherFW = findFW(aliasName, brain, server);

				if (otherFW == null) {
					forumWeapon.getAliases().add(aliasName);

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
	}

	private String cmdPrune(Lang lang, Brain brain, Guild server, ShmamesCommandMessagingChannel messagingChannel) {
		List<ForumWeaponObj> unused = getServerUnusedFWs(brain);

		sendPrunedFWs(server.getName(), messagingChannel, unused);

		for (ForumWeaponObj fw : unused) {
			brain.getForumWeapons().remove(fw);
		}

		return lang.getMsg(Langs.FORUM_WEAPONS_PRUNED, new String[]{Integer.toString(unused.size())});
	}

	private List<ForumWeaponObj> getServerUnusedFWs(Brain brain) {
		List<ForumWeaponObj> unused = new ArrayList<>();

		for(ForumWeaponObj obj : brain.getForumWeapons()) {
			if(obj.getUses() == 0) {
				unused.add(obj);
			}
		}

		return unused;
	}

	private ForumWeaponObj findFW(String name, Brain brain, Guild server) {
		// Check local server.
		for (ForumWeaponObj fw : brain.getForumWeapons()) {
			if (fw.getItemName().equals(name) || fw.getAliases().contains(name)) {
				return fw;
			}
		}

		// Check other Family servers.
		for (Guild family : Utils.GetConnectedFamilyGuilds(brain, server)) {
			Brain b = Shmames.getBrains().getBrain(family.getId());

			for (ForumWeaponObj fw : b.getForumWeapons()) {
				if (fw.getItemName().equals(name) || fw.getAliases().contains(name)) {
					return fw;
				}
			}
		}

		return null;
	}

	private ForumWeaponObj findFWByURL(String url, Brain brain, Guild server) {
		// Check local server.
		for (ForumWeaponObj fw : brain.getForumWeapons()) {
			if (fw.getItemLink().equals(url)) {
				return fw;
			}
		}

		// Check other Family servers.
		for (Guild family : Utils.GetConnectedFamilyGuilds(brain, server)) {
			Brain b = Shmames.getBrains().getBrain(family.getId());

			for (ForumWeaponObj fw : b.getForumWeapons()) {
				if (fw.getItemLink().equals(url)) {
					return fw;
				}
			}
		}

		return null;
	}

	private int getFWCount(Brain brain) {
		return brain.getForumWeapons().size();
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

	private String searchServerFWList(Brain brain, String serverName, String query) {
		HashMap<String, Integer> fwList = new HashMap<>();

		for(ForumWeaponObj fws : brain.getForumWeapons()) {
			if(fws.getItemName().contains(query.toLowerCase()))
				fwList.put(fws.getItemName(), fws.getUses());
		}

		LinkedHashMap<String, Integer> fwSorted = Utils.sortHashMap(fwList);
		String list = Utils.generateList(fwSorted, -1, true);

		return "**"+serverName+"**" + System.lineSeparator() + (list.length()>2 ? list.substring(2) : "> No Results");
	}

	private void sendPrunedFWs(String guildName, ShmamesCommandMessagingChannel messagingChannel, List<ForumWeaponObj> fws) {
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

		// Send the file.
		try {
			messagingChannel.sendFile(f, (success -> {f.delete();}), (failure -> {}));
		} catch(Exception e) {
			ShmamesLogger.logException(e);
		}
	}
}
