package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.command.ShmamesSubCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.PaginationService;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trigger implements ICommand {
	private final CommandStructure commandStructure;

	public Trigger() {
		CommandParameter triggerType = new CommandParameter("triggerType", "The type of trigger to create.", ParameterType.SELECTION);

		for(TriggerType type : TriggerType.values()) {
			triggerType.addSelectionOptions(type.name());
		}

		this.commandStructure = CommandBuilder.Create("trigger", "Manage response triggers.")
			.addSubCommands(
				CommandBuilder.Create("add", "Add a new trigger to the bot.")
					.addAlias("a")
					.addParameters(
							triggerType,
							new CommandParameter("triggerWord", "The trigger word to add.", ParameterType.STRING)
					)
					.build(),
				CommandBuilder.Create("drop", "Remove a trigger from the bot.")
					.addAlias("d")
					.addParameters(
							new CommandParameter("triggerWord", "The trigger word to remove.", ParameterType.STRING)
					)
					.build(),
				CommandBuilder.Create("list", "Display all of the current triggers.")
					.addAlias("l")
					.addParameters(
							new CommandParameter("page", "The page to navigate to.", ParameterType.INTEGER, false)
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
	public String getExamples() {
		return "`trigger add RANDOM Potato`\n" +
				"`trigger drop RANDOM Potato`\n" +
				"`trigger list 1`";
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
			case "list":
				cmdList(lang, brain, data.getMessagingChannel(), subCommand.getArguments());
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

	private void cmdAdd(Lang lang, Brain brain, ShmamesCommandMessagingChannel messagingChannel, ShmamesCommandArguments args) {
		String triggerType = args.getAsString("triggerType");
		String triggerWord = args.getAsString("triggerWord");

		if (!brain.getTriggers().containsKey(triggerWord)) {
			brain.getTriggers().put(triggerWord, TriggerType.byName(triggerType));

			messagingChannel.sendMessage(lang.getMsg(Langs.ADD_TRIGGER_SUCCESS, new String[]{triggerType, triggerWord}));
		} else {
			messagingChannel.sendMessage(lang.getError(Errors.ALREADY_EXISTS, true));
		}
	}

	private void cmdDrop(Lang lang, Brain brain, ShmamesCommandMessagingChannel messagingChannel, ShmamesCommandArguments args) {
		String triggerWord = args.getAsString("triggerWord");

		if(triggerWord.equalsIgnoreCase(Shmames.getBotName())) {
			messagingChannel.sendMessage(lang.getError(Errors.CANNOT_DELETE, true));
			return;
		}

		if (brain.getTriggers().containsKey(triggerWord)) {
			brain.getTriggers().remove(triggerWord);

			messagingChannel.sendMessage(lang.getMsg(Langs.ITEM_REMOVED, new String[]{ triggerWord }));
		} else {
			messagingChannel.sendMessage(lang.getError(Errors.NOT_FOUND, true));
		}
	}

	private void cmdList(Lang lang, Brain brain, ShmamesCommandMessagingChannel messagingChannel, ShmamesCommandArguments args) {
		int page = args.getAsInteger("page");
		HashMap<String, TriggerType> triggers = brain.getTriggers();
		List<String> triggersFormatted = formatTriggersToStringList(triggers);

		PaginatedList paginatedList = PaginationService.GetPaginatedList(triggersFormatted, 15, -1, false);

		messagingChannel.sendMessage(PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), lang.getMsg(Langs.TRIGGER_LIST), Color.ORANGE, lang));
	}

	private List<String> formatTriggersToStringList(HashMap<String, TriggerType> triggers) {
		List<String> triggerList = new ArrayList<>();

		for(String key : triggers.keySet()) {
			triggerList.add(key + ": **" + triggers.get(key).name() + "**");
		}

		return triggerList;
	}
}
