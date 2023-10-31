package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.fornax.service.caching.CacheService;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.language.ErrorKey;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.language.Language;
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

public class Tally extends Command {
	public Tally() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("tally", "Manage server tallies.")
			.addSubCommands(
					CommandBuilder.Create("add", "Increment a tally or create a new one.")
							.addAlias("a")
							.addParameters(
									new CommandParameter("tallyName", "The tally to adjust.", ParameterType.STRING)
											.setPattern("[\\w\\d\\s]{3,}")
											.setExample("myTally")
							)
							.build(),
					CommandBuilder.Create("drop", "Decrement a tally or delete it if 0.")
							.addAlias("d")
							.addParameters(
									new CommandParameter("tallyName", "The tally to adjust.", ParameterType.STRING)
											.setPattern("[\\w\\d\\s]{3,}")
											.setExample("myTally")
							)
							.build(),
					CommandBuilder.Create("set", "Overwrite the value of a tally.")
							.addAlias("s")
							.addParameters(
									new CommandParameter("tallyName", "The tally to adjust.", ParameterType.STRING)
											.setPattern("[\\w\\d\\s]{3,}")
											.setExample("myTally"),
									new CommandParameter("count", "The new count for this tally.", ParameterType.INTEGER)
											.setExample("3")
							)
							.build(),
					CommandBuilder.Create("list", "Display all of the current tallies.")
							.addAlias("l")
							.addParameters(
									new CommandParameter("page", "The page to navigate to.", ParameterType.INTEGER, false)
											.setExample("2")
							)
							.build(),
					CommandBuilder.Create("search", "Find a tally based on a partial match.")
							.addParameters(
									new CommandParameter("tallyName", "The tally to search for.", ParameterType.STRING)
											.setPattern("[\\w\\d\\s]{3,}")
											.setExample("tally")
							)
							.build(),
					CommandBuilder.Create("reset", "Export current tallies and clear them out.")
							.build()
				)
				.build();
	}

	@Override
	public EmbedBuilder run(ExecutingCommand executingCommand) {
		String subCommand = executingCommand.getSubCommand();
		Language language = executingCommand.getLanguage();
		Brain brain = executingCommand.getBrain();

		switch (subCommand) {
			case "add":
				return cmdAdd(language, brain, executingCommand.getCommandArguments());
			case "drop":
				return cmdDrop(language, brain, executingCommand.getCommandArguments());
			case "set":
				return cmdSet(language, brain, executingCommand.getCommandArguments());
			case "list":
				return cmdList(language, brain, executingCommand);
			case "search":
				return cmdSearch(language, brain, executingCommand.getCommandArguments());
			case "reset":
				return cmdReset(language, brain, executingCommand);
		}

		return null;
	}

	private EmbedBuilder cmdAdd(Language language, Brain brain, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		String tally = formatTally(rawTally);
		int newTally = brain.getTallies().getOrDefault(tally, 0) + 1;

		brain.getTallies().put(tally, newTally);

		return response(EmbedType.SUCCESS)
				.setDescription(language.getMsg(LanguageKey.TALLY_CURRENT_VALUE, new String[]{tally, Integer.toString(newTally)}));
	}

	private EmbedBuilder cmdDrop(Language language, Brain brain, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		String tally = formatTally(rawTally);
		int newTally = brain.getTallies().getOrDefault(tally, 0) - 1;

		if (newTally == -1) {
			// Never existed

			return response(EmbedType.ERROR, ErrorKey.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKey.NOT_FOUND));
		} else if (newTally == 0) {
			// Existed and removed
			brain.getTallies().remove(tally);

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKey.TALLY_REMOVED, new String[]{tally}));
		} else {
			// Exists and lowers by 1
			brain.getTallies().put(tally, newTally);

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKey.TALLY_CURRENT_VALUE, new String[]{tally, Integer.toString(newTally)}));
		}
	}

	private EmbedBuilder cmdSet(Language language, Brain brain, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		int count = args.getAsInteger("count");
		String tally = formatTally(rawTally);

		if (count > 0) {
			brain.getTallies().put(tally, count);

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKey.TALLY_CURRENT_VALUE, new String[]{tally, Integer.toString(count)}));
		} else {
			brain.getTallies().remove(tally);

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKey.TALLY_REMOVED, new String[]{tally}));
		}
	}

	private EmbedBuilder cmdList(Language language, Brain brain, ExecutingCommand executingCommand) {
		int page = executingCommand.getCommandArguments().getAsInteger("page");
		final String cacheKey = CacheService.GenerateCacheKey(executingCommand.getServer().getIdLong(), executingCommand.getChannel().getIdLong(), executingCommand.getAuthorUser().getIdLong(), "tally-list");
		final PaginatedList cachedList = CacheService.RetrieveItem(cacheKey, PaginatedList.class);

		PaginatedList paginatedList;

		// If this list has been cached, retrieve it instead of building another one.
		if (cachedList != null) {
			paginatedList = cachedList;
		} else {
			LinkedHashMap<String, Integer> tSorted = DataService.SortHashMap(brain.getTallies());
			List<String> talliesFormatted = formatTalliesToStringList(tSorted);

			paginatedList = PaginationService.GetPaginatedList(talliesFormatted, 15, -1, false);

			// Cache the list in case the user continues to call this command for other pages
			CacheService.StoreItem(cacheKey, paginatedList);
		}

		return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), language.getMsg(LanguageKey.TALLY_LIST), Color.ORANGE, language);
	}

	private EmbedBuilder cmdSearch(Language language, Brain brain, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		String tally = formatTally(rawTally);
		List<String> searchResults = new ArrayList<>();
		LinkedHashMap<String, Integer> tSorted = DataService.SortHashMap(brain.getTallies());

		for(String tallyKey : tSorted.keySet()) {
			if(tallyKey.contains(tally)) {
				searchResults.add(tallyKey + ": **" + tSorted.get(tallyKey) + "**");
			}
		}

		EmbedBuilder eBuilder = response(EmbedType.INFO, "Search");

		eBuilder.addField(language.getMsg(LanguageKey.SEARCH_RESULTS), PaginationService.CompileListToString(searchResults),false);

		return eBuilder;
	}

	private EmbedBuilder cmdReset(Language language, Brain brain, ExecutingCommand executingCommand) {
		Guild server = executingCommand.getServer();

		if (ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.RESET_TALLIES), executingCommand.getAuthorMember())) {
			HashMap<String, Integer> tallies = brain.getTallies();
			File file = buildTalliesList(server.getName(), tallies);

			EmbedBuilder response = response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKey.TALLIES_CLEARED, new String[]{Integer.toString(tallies.size())}));
			executingCommand.replyFile(file, response);

			tallies.clear();

			return null;
		} else {
			return response(EmbedType.ERROR, ErrorKey.NO_PERMISSION_USER.name())
					.setDescription(language.getError(ErrorKey.NO_PERMISSION_USER));
		}
	}

	private List<String> formatTalliesToStringList(LinkedHashMap<String, Integer> tallies) {
		List<String> tallyList = new ArrayList<>();

		for(String key : tallies.keySet()) {
			tallyList.add(key + ": **" + tallies.get(key) + "**");
		}

		return tallyList;
	}

	private String formatTally(String rawTally) {
		return rawTally.trim().replaceAll("\\s", "_").replaceAll("\\W", "").toLowerCase();
	}

	private File buildTalliesList(String guildName, HashMap<String, Integer> tallies) {
		StringBuilder pruned = new StringBuilder("Pruned Tallies\n");

		pruned.append("=======================\n");
		pruned.append("= Count:\t\tName =\n");
		pruned.append("=======================\n");

		// Build list.
		for(String tally : tallies.keySet()) {
			pruned.append("\n");
			pruned.append(tallies.get(tally));
			pruned.append(":\t");
			pruned.append(tally);
		}

		// Save to file.
		return FileService.SaveBytesToFile("reports", guildName+".txt", pruned.toString().getBytes());
	}
}
