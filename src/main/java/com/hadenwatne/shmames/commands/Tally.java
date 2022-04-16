package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.command.ShmamesSubCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Tally implements ICommand {
	private final CommandStructure commandStructure;

	public Tally() {
		this.commandStructure = CommandBuilder.Create("tally", "Manage server tallies.")
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
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		ShmamesSubCommandData subCommand = data.getSubCommandData();
		String nameOrGroup = subCommand.getNameOrGroup();

		switch (nameOrGroup) {
			case "add":
				cmdAdd(lang, brain, data.getMessagingChannel(), subCommand.getArguments());
				break;
			case "drop":
				cmdDrop(lang, brain, data.getMessagingChannel(), subCommand.getArguments());
				break;
			case "set":
				cmdSet(lang, brain, data.getMessagingChannel(), subCommand.getArguments());
				break;
			case "list":
				cmdList(lang, brain, data.getMessagingChannel(), subCommand.getArguments());
				break;
			case "search":
				cmdSearch(lang, brain, data.getMessagingChannel(), subCommand.getArguments());
				break;
			default:
				return lang.wrongUsage(commandStructure.getUsage());
		}

		return "";
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}

	private void cmdAdd(Lang lang, Brain brain, ShmamesCommandMessagingChannel messagingChannel, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		String tally = formatTally(rawTally);
		int newTally = brain.getTallies().getOrDefault(tally, 0) + 1;

		brain.getTallies().put(tally, newTally);

		messagingChannel.sendMessage(lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{tally, Integer.toString(newTally)}));
	}

	private void cmdDrop(Lang lang, Brain brain, ShmamesCommandMessagingChannel messagingChannel, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		String tally = formatTally(rawTally);
		int newTally = brain.getTallies().getOrDefault(tally, 0) - 1;

		if (newTally == -1) {
			// Never existed
			messagingChannel.sendMessage(lang.getError(Errors.NOT_FOUND, true));
		} else if (newTally == 0) {
			// Existed and removed
			brain.getTallies().remove(tally);

			messagingChannel.sendMessage(lang.getMsg(Langs.TALLY_REMOVED, new String[]{tally}));
		} else {
			// Exists and lowers by 1
			brain.getTallies().put(tally, newTally);

			messagingChannel.sendMessage(lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{ tally, Integer.toString(newTally) }));
		}
	}

	private void cmdSet(Lang lang, Brain brain, ShmamesCommandMessagingChannel messagingChannel, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		int count = args.getAsInteger("count");
		String tally = formatTally(rawTally);

		if (count > 0) {
			brain.getTallies().put(tally, count);

			messagingChannel.sendMessage(lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{tally, Integer.toString(count)}));
		} else {
			brain.getTallies().remove(tally);

			messagingChannel.sendMessage(lang.getMsg(Langs.TALLY_REMOVED, new String[]{tally}));
		}
	}

	private void cmdList(Lang lang, Brain brain, ShmamesCommandMessagingChannel messagingChannel, ExecutingCommandArguments args) {
		int page = args.getAsInteger("page");
		LinkedHashMap<String, Integer> tSorted = DataService.SortHashMap(brain.getTallies());
		List<String> talliesFormatted = formatTalliesToStringList(tSorted);

		PaginatedList paginatedList = PaginationService.GetPaginatedList(talliesFormatted, 15, -1, false);

		messagingChannel.sendMessage(PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), lang.getMsg(Langs.TALLY_LIST), Color.ORANGE, lang));
	}

	private void cmdSearch(Lang lang, Brain brain, ShmamesCommandMessagingChannel messagingChannel, ExecutingCommandArguments args) {
		String rawTally = args.getAsString("tallyName");
		String tally = formatTally(rawTally);
		List<String> searchResults = new ArrayList<>();
		LinkedHashMap<String, Integer> tSorted = DataService.SortHashMap(brain.getTallies());

		for(String tallyKey : tSorted.keySet()) {
			if(tallyKey.contains(tally)) {
				String formattedKey = tallyKey.replace(tally, "**" + tally + "**");
				searchResults.add(formattedKey + ": **" + tSorted.get(tallyKey) + "**");
			}
		}

		EmbedBuilder eBuilder = new EmbedBuilder();

		eBuilder.setColor(Color.ORANGE);
		eBuilder.setAuthor(App.Shmames.getBotName(), null, App.Shmames.getJDA().getSelfUser().getAvatarUrl());

		eBuilder.addField(lang.getMsg(Langs.SEARCH_RESULTS), PaginationService.CompileListToString(searchResults),false);

		messagingChannel.sendMessage(eBuilder);
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
