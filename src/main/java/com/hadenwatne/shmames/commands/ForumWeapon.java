package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.*;
import com.hadenwatne.shmames.models.ForumWeaponObj;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.services.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class ForumWeapon extends Command {
	private final int MAX_WEAPON_COUNT = 100;

	public ForumWeapon() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("forumweapon", "Create shorthand names for your favorite links, and share them in a snap.")
				.addAlias("fw")
				.addSubCommands(
						CommandBuilder.Create("create", "Create a new Forum Weapon.")
								.addParameters(
										new CommandParameter("weaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}")
												.setExample("myWeapon"),
										new CommandParameter("weaponURL", "The URL this weapon directs to.", ParameterType.STRING)
												.setPattern(RegexPatterns.URL.getPattern())
												.setExample("url/")
								)
								.build(),
						CommandBuilder.Create("update", "Change the URL of an existing Forum Weapon.")
								.addParameters(
										new CommandParameter("weaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}")
												.setExample("myWeapon"),
										new CommandParameter("weaponURL", "The URL this weapon directs to.", ParameterType.STRING)
												.setPattern(RegexPatterns.URL.getPattern())
												.setExample("url/")
								)
								.build(),
						CommandBuilder.Create("remove", "Delete a Forum Weapon.")
								.addParameters(
										new CommandParameter("weaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}")
												.setExample("myWeapon")
								)
								.build(),
						CommandBuilder.Create("list", "List all available Forum Weapons.")
								.addParameters(
										new CommandParameter("all", "Whether to search all connected guilds.", ParameterType.BOOLEAN, false)
												.setExample("true"),
										new CommandParameter("page", "The page to navigate to.", ParameterType.INTEGER, false)
												.setExample("2")
								)
								.build(),
						CommandBuilder.Create("search", "Search for a Forum Weapon.")
								.addParameters(
										new CommandParameter("searchTerm", "The partial name of the Forum Weapon to search for.", ParameterType.STRING)
												.setPattern("\\w{3,}")
												.setExample("explo"),
										new CommandParameter("page", "The page to navigate to.", ParameterType.INTEGER, false)
												.setExample("2")
								)
								.build(),
						CommandBuilder.Create("alias", "Add an alternative name for a Forum Weapon.")
								.addParameters(
										new CommandParameter("weaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}")
												.setExample("myWeapon"),
										new CommandParameter("newAlias", "The URL this weapon directs to.", ParameterType.STRING)
												.setPattern("\\w{3,}")
												.setExample("coolWeapon")
								)
								.build(),
						CommandBuilder.Create("send", "Send a Forum Weapon.")
								.addAlias("s")
								.addParameters(
										new CommandParameter("weaponName", "The name of the Forum Weapon.", ParameterType.STRING)
												.setPattern("\\w{3,}")
												.setExample("myWeapon")
								)
								.build(),
						CommandBuilder.Create("prune", "List and delete unused Forum Weapons.")
								.build()
				)
				.build();
	}

	@Override
	public EmbedBuilder run(ExecutingCommand executingCommand) {
		String subCommand = executingCommand.getSubCommand();
		Brain brain = executingCommand.getBrain();
		Language language = executingCommand.getLanguage();
		Guild server = executingCommand.getServer();

		switch (subCommand) {
			case "create":
				return cmdCreate(language, brain, server, executingCommand.getCommandArguments());
			case "update":
				return cmdUpdate(language, brain, server, executingCommand.getCommandArguments());
			case "destroy":
			case "remove":
				return cmdRemove(language, brain, server, executingCommand.getCommandArguments());
			case "list":
				return cmdList(language, brain, server, executingCommand);
			case "search":
				return cmdSearch(language, brain, server, executingCommand.getCommandArguments());
			case "alias":
				return cmdAlias(language, brain, server, executingCommand.getCommandArguments());
			case "prune":
				return cmdPrune(language, brain, server, executingCommand);
			case "send":
				return cmdSend(language, brain, server, executingCommand);
		}

		return null;
	}

	private EmbedBuilder cmdCreate(Language language, Brain brain, Guild server, ExecutingCommandArguments args) {
		String weaponName = args.getAsString("weaponName").toLowerCase();
		String weaponURL = args.getAsString("weaponURL");

		if (getFWCount(brain) < MAX_WEAPON_COUNT) {
			if (weaponName.equals("create") || weaponName.equals("update") || weaponName.equals("remove")
					|| weaponName.equals("list") || weaponName.equals("search") || weaponName.equals("prune")) {
				return response(EmbedType.ERROR, ErrorKeys.RESERVED_WORD.name())
						.setDescription(language.getError(ErrorKeys.RESERVED_WORD));
			}

			if (FindForumWeapon(weaponName, brain, server) == null) {
				ForumWeaponObj newWeapon = new ForumWeaponObj(weaponName, weaponURL, server.getId());
				ForumWeaponObj existingUrl = findFWByURL(weaponURL, brain, server);

				brain.getForumWeapons().add(newWeapon);

				EmbedBuilder response = response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.FORUM_WEAPON_CREATED, new String[]{weaponName}));

				if (existingUrl != null) {
					response.addField("Duplicate Found", language.getMsg(LanguageKeys.FORUM_WEAPON_DUPLICATE, new String[]{existingUrl.getItemName()}), false);
				}

				return response;
			} else {
				return response(EmbedType.ERROR, ErrorKeys.ALREADY_EXISTS.name())
						.setDescription(language.getError(ErrorKeys.ALREADY_EXISTS));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKeys.FORUM_WEAPON_MAXIMUM_REACHED.name())
					.setDescription(language.getError(ErrorKeys.FORUM_WEAPON_MAXIMUM_REACHED, new String[]{Integer.toString(MAX_WEAPON_COUNT)}));
		}
	}

	private EmbedBuilder cmdUpdate(Language language, Brain brain, Guild server, ExecutingCommandArguments args) {
		String weaponName = args.getAsString("weaponName").toLowerCase();
		String weaponURL = args.getAsString("weaponURL");
		ForumWeaponObj forumWeapon = FindForumWeapon(weaponName, brain, server);

		if (forumWeapon != null) {
			if (forumWeapon.getServerID().equals(server.getId())) {
				forumWeapon.setItemLink(weaponURL);

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.FORUM_WEAPON_UPDATED));
			} else {
				return response(EmbedType.ERROR, ErrorKeys.FORUM_WEAPON_OWNED_OTHER.name())
						.setDescription(language.getError(ErrorKeys.FORUM_WEAPON_OWNED_OTHER));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdRemove(Language language, Brain brain, Guild server, ExecutingCommandArguments args) {
		String weaponName = args.getAsString("weaponName").toLowerCase();
		ForumWeaponObj fwr = FindForumWeapon(weaponName, brain, server);

		if (fwr != null) {
			if (fwr.getServerID().equals(server.getId())) {
				brain.getForumWeapons().remove(fwr);

				return response(EmbedType.SUCCESS)
						.setDescription(language.getMsg(LanguageKeys.FORUM_WEAPON_DESTROYED));
			} else {
				return response(EmbedType.ERROR, ErrorKeys.FORUM_WEAPON_OWNED_OTHER.name())
						.setDescription(language.getError(ErrorKeys.FORUM_WEAPON_OWNED_OTHER));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdList(Language language, Brain brain, Guild server, ExecutingCommand executingCommand) {
		boolean all = executingCommand.getCommandArguments().getAsBoolean("all");
		int page = executingCommand.getCommandArguments().getAsInteger("page");
		final String cacheKey = CacheService.GenerateCacheKey(executingCommand.getServer().getIdLong(), executingCommand.getChannel().getIdLong(), executingCommand.getAuthorUser().getIdLong(), "fw-list", all ? "Y" : "N");
		final PaginatedList cachedList = CacheService.RetrieveItem(cacheKey, PaginatedList.class);

		PaginatedList paginatedList;

		// If this list has been cached, retrieve it instead of building another one.
		if (cachedList != null) {
			paginatedList = cachedList;
		} else {

			List<String> forumWeaponList = new ArrayList<>();

			if (all) {
				// Start by listing this server.
				LinkedHashMap<String, Integer> thisServer = buildServerFWList(server);

				forumWeaponList.add("**" + server.getName() + "**");

				for (String fwKey : thisServer.keySet()) {
					forumWeaponList.add(fwKey + ": **" + thisServer.get(fwKey) + "**");
				}

				// List the Family.
				for(Guild family : ShmamesService.GetConnectedFamilyGuilds(brain, server)) {
					LinkedHashMap<String, Integer> familyServer = buildServerFWList(server);

					forumWeaponList.add("**" + family.getName() + "**");

					for (String fwKey : familyServer.keySet()) {
						forumWeaponList.add(fwKey + ": **" + familyServer.get(fwKey) + "**");
					}
				}
			} else {
				// Just list this Guild
				LinkedHashMap<String, Integer> sorted = buildServerFWList(server);

				forumWeaponList.add("**" + server.getName() + "**");

				for (String fwKey : sorted.keySet()) {
					forumWeaponList.add(fwKey + ": **" + sorted.get(fwKey) + "**");
				}
			}

			paginatedList = PaginationService.GetPaginatedList(forumWeaponList, 15, -1, false);

			// Cache the list in case the user continues to call this command for other pages
			CacheService.StoreItem(cacheKey, paginatedList);
		}

		return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), language.getMsg(LanguageKeys.FORUM_WEAPON_LIST), Color.ORANGE, language);
	}

	private EmbedBuilder cmdSearch(Language language, Brain brain, Guild server, ExecutingCommandArguments args) {
		String searchTerm = args.getAsString("searchTerm");
		int page = args.getAsInteger("page");
		List<String> forumWeaponList = new ArrayList<>();

		// Search this server.
		LinkedHashMap<String, Integer> thisServer = searchServerFWList(brain, searchTerm);

		forumWeaponList.add("**" + server.getName() + "**");

		for (String fwKey : thisServer.keySet()) {
			forumWeaponList.add(fwKey + ": **" + thisServer.get(fwKey) + "**");
		}

		// Search the Family.
		for(Guild family : ShmamesService.GetConnectedFamilyGuilds(brain, server)) {
			Brain familyBrain = App.Shmames.getStorageService().getBrain(family.getId());
			LinkedHashMap<String, Integer> familyServer = searchServerFWList(familyBrain, searchTerm);

			forumWeaponList.add("**" + family.getName() + "**");

			for (String fwKey : familyServer.keySet()) {
				forumWeaponList.add(fwKey + ": **" + familyServer.get(fwKey) + "**");
			}
		}

		PaginatedList paginatedList = PaginationService.GetPaginatedList(forumWeaponList, 15, -1, false);

		return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), language.getMsg(LanguageKeys.FORUM_WEAPON_LIST), Color.ORANGE, language);
	}

	private EmbedBuilder cmdAlias(Language language, Brain brain, Guild server, ExecutingCommandArguments args) {
		String weaponName = args.getAsString("weaponName").toLowerCase();
		String aliasName = args.getAsString("newAlias");
		ForumWeaponObj forumWeapon = FindForumWeapon(weaponName, brain, server);

		if (forumWeapon != null) {
			if (!forumWeapon.getAliases().contains(aliasName)) {
				ForumWeaponObj otherFW = FindForumWeapon(aliasName, brain, server);

				if (otherFW == null) {
					forumWeapon.getAliases().add(aliasName);

					return response(EmbedType.SUCCESS)
							.setDescription(language.getMsg(LanguageKeys.FORUM_WEAPON_ADDED_ALIAS));
				} else {
					return response(EmbedType.ERROR, ErrorKeys.ALREADY_EXISTS.name())
							.setDescription(language.getError(ErrorKeys.ALREADY_EXISTS));
				}
			} else {
				return response(EmbedType.ERROR, ErrorKeys.ALREADY_EXISTS.name())
						.setDescription(language.getError(ErrorKeys.ALREADY_EXISTS));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdPrune(Language language, Brain brain, Guild server, ExecutingCommand executingCommand) {
		if (ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.PRUNE_FW), executingCommand.getAuthorMember())) {
			List<ForumWeaponObj> unused = getServerUnusedFWs(brain);
			File file = buildPrunedWeaponFile(server.getName(), unused);

			EmbedBuilder response = response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKeys.FORUM_WEAPONS_PRUNED, new String[]{Integer.toString(unused.size())}));
			executingCommand.replyFile(file, response);

			for (ForumWeaponObj fw : unused) {
				brain.getForumWeapons().remove(fw);
			}

			return null;
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NO_PERMISSION_USER.name())
					.setDescription(language.getError(ErrorKeys.NO_PERMISSION_USER));
		}
	}

	private EmbedBuilder cmdSend(Language language, Brain brain, Guild server, ExecutingCommand executingCommand) {
		String weaponName = executingCommand.getCommandArguments().getAsString("weaponName");
		ForumWeaponObj fws = FindForumWeapon(weaponName, brain, server);

		if (fws != null) {
			fws.IncreaseUse();

			executingCommand.reply(fws.getItemLink());
			return null;
		} else {
			// Couldn't find one
			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		}
	}

	public static ForumWeaponObj FindForumWeapon(String name, Brain brain, Guild server) {
		// Check local server.
		for (ForumWeaponObj fw : brain.getForumWeapons()) {
			if (fw.getItemName().equals(name) || fw.getAliases().contains(name)) {
				return fw;
			}
		}

		// Check other Family servers.
		for (Guild family : ShmamesService.GetConnectedFamilyGuilds(brain, server)) {
			Brain b = App.Shmames.getStorageService().getBrain(family.getId());

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
		for (Guild family : ShmamesService.GetConnectedFamilyGuilds(brain, server)) {
			Brain b = App.Shmames.getStorageService().getBrain(family.getId());

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

	private LinkedHashMap<String, Integer> buildServerFWList(Guild server) {
		HashMap<String, Integer> fwList = new HashMap<>();

		for(ForumWeaponObj fws : App.Shmames.getStorageService().getBrain(server.getId()).getForumWeapons()) {
			fwList.put(fws.getItemName(), fws.getUses());
		}

		return DataService.SortHashMap(fwList);
	}

	private LinkedHashMap<String, Integer> searchServerFWList(Brain brain, String query) {
		HashMap<String, Integer> fwList = new HashMap<>();

		for(ForumWeaponObj fws : brain.getForumWeapons()) {
			if(fws.getItemName().contains(query.toLowerCase()))
				fwList.put(fws.getItemName(), fws.getUses());
		}

		return DataService.SortHashMap(fwList);
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

	private File buildPrunedWeaponFile(String guildName, List<ForumWeaponObj> unused) {
		StringBuilder pruned = new StringBuilder("Pruned ForumWeapons\n");

		pruned.append("=======================\n");
		pruned.append("= Name:\t\tURL   =\n");
		pruned.append("=======================\n");

		// Build list.
		for(ForumWeaponObj fw : unused) {
			pruned.append("\n");
			pruned.append(fw.getItemName());
			pruned.append(":\t\t");
			pruned.append(fw.getItemLink());
		}

		// Save to file.
		return FileService.SaveBytesToFile("reports", guildName+".txt", App.gson, pruned.toString(), String.class);
	}
}
