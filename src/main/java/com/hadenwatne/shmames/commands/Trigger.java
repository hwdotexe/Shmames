package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trigger extends Command {
	public Trigger() {
		super(true);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		CommandParameter triggerType = new CommandParameter("triggerType", "The type of trigger to create.", ParameterType.SELECTION);

		for(TriggerType type : TriggerType.values()) {
			triggerType.addSelectionOptions(type.name());
		}

		return CommandBuilder.Create("trigger", "Manage response triggers.")
				.addSubCommands(
						CommandBuilder.Create("add", "Add a new trigger to the bot.")
								.addAlias("a")
								.addParameters(
										triggerType
												.setExample("random"),
										new CommandParameter("triggerWord", "The trigger word to add.", ParameterType.STRING)
												.setExample("pizza")
								)
								.build(),
						CommandBuilder.Create("drop", "Remove a trigger from the bot.")
								.addAlias("d")
								.addParameters(
										new CommandParameter("triggerWord", "The trigger word to remove.", ParameterType.STRING)
												.setExample("pizza")
								)
								.build(),
						CommandBuilder.Create("list", "Display all of the current triggers.")
								.addAlias("l")
								.addParameters(
										new CommandParameter("page", "The page to navigate to.", ParameterType.INTEGER, false)
												.setExample("2")
								)
								.build()
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String nameOrGroup = executingCommand.getSubCommand();
		Lang language = executingCommand.getLanguage();
		Brain brain = executingCommand.getBrain();

		switch (nameOrGroup) {
			case "add":
				return cmdAdd(language, brain, executingCommand.getCommandArguments());
			case "drop":
				return cmdDrop(language, brain, executingCommand.getCommandArguments());
			case "list":
				return cmdList(language, brain, executingCommand.getCommandArguments());
		}

		return null;
	}

	private EmbedBuilder cmdAdd(Lang lang, Brain brain, ExecutingCommandArguments args) {
		String triggerType = args.getAsString("triggerType");
		String triggerWord = args.getAsString("triggerWord");

		if (!brain.getTriggers().containsKey(triggerWord)) {
			brain.getTriggers().put(triggerWord, TriggerType.byName(triggerType));

			String response = lang.getMsg(Langs.ADD_TRIGGER_SUCCESS, new String[]{triggerType, triggerWord});

			return response(EmbedType.SUCCESS)
					.setDescription(response);
		} else {
			String response = lang.getError(Errors.ALREADY_EXISTS);

			return response(EmbedType.ERROR)
					.addField(Errors.ALREADY_EXISTS.name(), response, false);
//					.setDescription(response);
		}
	}

	private EmbedBuilder cmdDrop(Lang lang, Brain brain, ExecutingCommandArguments args)  {
		String triggerWord = args.getAsString("triggerWord");

		if(triggerWord.equalsIgnoreCase(App.Shmames.getBotName())) {
			String response = lang.getError(Errors.CANNOT_DELETE);

			return response(EmbedType.ERROR)
					.addField(Errors.CANNOT_DELETE.name(), response, false);
		}

		if (brain.getTriggers().containsKey(triggerWord)) {
			brain.getTriggers().remove(triggerWord);

			String response =  lang.getMsg(Langs.ITEM_REMOVED, new String[]{ triggerWord });

			return response(EmbedType.SUCCESS)
					.setDescription(response);
		} else {
			String response = lang.getError(Errors.NOT_FOUND);

			return response(EmbedType.ERROR)
					.addField(Errors.NOT_FOUND.name(), response, false);
		}
	}

	private EmbedBuilder cmdList(Lang lang, Brain brain, ExecutingCommandArguments args) {
		int page = args.getAsInteger("page");
		HashMap<String, TriggerType> triggers = brain.getTriggers();
		List<String> triggersFormatted = formatTriggersToStringList(triggers);

		PaginatedList paginatedList = PaginationService.GetPaginatedList(triggersFormatted, 15, -1, false);

		return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), lang.getMsg(Langs.TRIGGER_LIST), Color.ORANGE, lang);
	}

	private List<String> formatTriggersToStringList(HashMap<String, TriggerType> triggers) {
		List<String> triggerList = new ArrayList<>();

		for(String key : triggers.keySet()) {
			triggerList.add(key + ": **" + triggers.get(key).name() + "**");
		}

		return triggerList;
	}
}
