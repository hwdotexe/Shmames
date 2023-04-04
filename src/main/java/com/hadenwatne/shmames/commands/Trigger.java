package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ExecutingCommandArguments;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.services.CacheService;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Trigger extends Command {
	public Trigger() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
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
		Language language = executingCommand.getLanguage();
		Brain brain = executingCommand.getBrain();

		switch (nameOrGroup) {
			case "add":
				return cmdAdd(language, brain, executingCommand.getCommandArguments());
			case "drop":
				return cmdDrop(language, brain, executingCommand.getCommandArguments());
			case "list":
				return cmdList(language, brain, executingCommand);
		}

		return null;
	}

	private EmbedBuilder cmdAdd(Language language, Brain brain, ExecutingCommandArguments args) {
		String triggerType = args.getAsString("triggerType");
		String triggerWord = args.getAsString("triggerWord");

		if (!brain.getTriggers().containsKey(triggerWord)) {
			brain.getTriggers().put(triggerWord, TriggerType.byName(triggerType));

			String response = language.getMsg(LanguageKeys.TRIGGER_ADD_SUCCESS, new String[]{triggerType, triggerWord});

			return response(EmbedType.SUCCESS)
					.setDescription(response);
		} else {
			return response(EmbedType.ERROR, ErrorKeys.ALREADY_EXISTS.name())
					.setDescription(language.getError(ErrorKeys.ALREADY_EXISTS));
		}
	}

	private EmbedBuilder cmdDrop(Language language, Brain brain, ExecutingCommandArguments args)  {
		String triggerWord = args.getAsString("triggerWord");

		if(triggerWord.equalsIgnoreCase(App.Shmames.getBotName())) {
			return response(EmbedType.ERROR, ErrorKeys.CANNOT_DELETE.name())
					.setDescription(language.getError(ErrorKeys.CANNOT_DELETE));
		}

		if (brain.getTriggers().containsKey(triggerWord)) {
			brain.getTriggers().remove(triggerWord);

			String response =  language.getMsg(LanguageKeys.ITEM_REMOVED, new String[]{ triggerWord });

			return response(EmbedType.SUCCESS)
					.setDescription(response);
		} else {
			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdList(Language language, Brain brain, ExecutingCommand executingCommand) {
		int page = executingCommand.getCommandArguments().getAsInteger("page");
		final String cacheKey = CacheService.GenerateCacheKey(executingCommand.getServer().getIdLong(), executingCommand.getChannel().getIdLong(), executingCommand.getAuthorUser().getIdLong(), "trigger-list");
		final PaginatedList cachedList = CacheService.RetrieveItem(cacheKey, PaginatedList.class);

		PaginatedList paginatedList;

		// If this list has been cached, retrieve it instead of building another one.
		if (cachedList != null) {
			paginatedList = cachedList;
		} else {
			HashMap<String, TriggerType> triggers = brain.getTriggers();
			List<String> triggersFormatted = formatTriggersToStringList(triggers);

			paginatedList = PaginationService.GetPaginatedList(triggersFormatted, 15, -1, false);

			// Cache the list in case the user continues to call this command for other pages
			CacheService.StoreItem(cacheKey, paginatedList);
		}

		return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), language.getMsg(LanguageKeys.TRIGGER_LIST), Color.ORANGE, language);
	}

	private List<String> formatTriggersToStringList(HashMap<String, TriggerType> triggers) {
		List<String> triggerList = new ArrayList<>();

		for(String key : triggers.keySet()) {
			triggerList.add(key + ": **" + triggers.get(key).name() + "**");
		}

		return triggerList;
	}
}
