package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.CacheService;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Tally extends Command {
	public Tally() {
		super(true);
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
							.build()
				)
				.build();
	}

	@Override
	public EmbedBuilder run(ExecutingCommand executingCommand) {
		String subCommand = executingCommand.getSubCommand();
		Lang lang = executingCommand.getLanguage();
		Brain brain = executingCommand.getBrain();

		switch (subCommand) {
			case "add":
				return cmdAdd(lang, brain, executingCommand.getCommandArguments());
			case "drop":
				return cmdDrop(lang, brain, executingCommand.getCommandArguments());
			case "set":
				return cmdSet(lang, brain, executingCommand.getCommandArguments());
			case "list":
				return cmdList(lang, brain, executingCommand);
			case "search":
				return cmdSearch(lang, brain, executingCommand.getCommandArguments());
		}

		return null;
	}

	private EmbedBuilder cmdAdd(Lang lang, Brain brain, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		String tally = formatTally(rawTally);
		int newTally = brain.getTallies().getOrDefault(tally, 0) + 1;

		brain.getTallies().put(tally, newTally);

		return response(EmbedType.SUCCESS)
				.setDescription(lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{tally, Integer.toString(newTally)}));
	}

	private EmbedBuilder cmdDrop(Lang lang, Brain brain, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		String tally = formatTally(rawTally);
		int newTally = brain.getTallies().getOrDefault(tally, 0) - 1;

		if (newTally == -1) {
			// Never existed

			return response(EmbedType.ERROR, Errors.NOT_FOUND.name())
					.setDescription(lang.getError(Errors.NOT_FOUND));
		} else if (newTally == 0) {
			// Existed and removed
			brain.getTallies().remove(tally);

			return response(EmbedType.SUCCESS)
					.setDescription(lang.getMsg(Langs.TALLY_REMOVED, new String[]{tally}));
		} else {
			// Exists and lowers by 1
			brain.getTallies().put(tally, newTally);

			return response(EmbedType.SUCCESS)
					.setDescription(lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{tally, Integer.toString(newTally)}));
		}
	}

	private EmbedBuilder cmdSet(Lang lang, Brain brain, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		int count = args.getAsInteger("count");
		String tally = formatTally(rawTally);

		if (count > 0) {
			brain.getTallies().put(tally, count);

			return response(EmbedType.SUCCESS)
					.setDescription(lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{tally, Integer.toString(count)}));
		} else {
			brain.getTallies().remove(tally);

			return response(EmbedType.SUCCESS)
					.setDescription(lang.getMsg(Langs.TALLY_REMOVED, new String[]{tally}));
		}
	}

	private EmbedBuilder cmdList(Lang lang, Brain brain, ExecutingCommand executingCommand) {
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

		return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), lang.getMsg(Langs.TALLY_LIST), Color.ORANGE, lang);
	}

	private EmbedBuilder cmdSearch(Lang lang, Brain brain, ExecutingCommandArguments args) {
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

		eBuilder.addField(lang.getMsg(Langs.SEARCH_RESULTS), PaginationService.CompileListToString(searchResults),false);

		return eBuilder;
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
}
