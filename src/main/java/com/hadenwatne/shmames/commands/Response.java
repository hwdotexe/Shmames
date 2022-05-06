package com.hadenwatne.shmames.commands;

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
import com.hadenwatne.shmames.services.CacheService;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Response extends Command {
	public Response() {
		super(true);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		CommandParameter triggerType = new CommandParameter("triggerType", "The type of trigger to provoke this response", ParameterType.SELECTION);

		for(TriggerType type : TriggerType.values()) {
			triggerType.addSelectionOptions(type.name());
		}

		return CommandBuilder.Create("response", "Manage bot responses.")
			.addSubCommands(
				CommandBuilder.Create("add", "Add a new response to the random pool.")
						.addAlias("a")
						.addParameters(
								triggerType
										.setExample("random"),
								new CommandParameter("responseText", "The actual text of this response.", ParameterType.STRING)
										.setExample("hello!")
						)
						.build(),
				CommandBuilder.Create("drop", "Remove a response from the random pool.")
						.addAlias("d")
						.addParameters(
								triggerType
										.setExample("random"),
								new CommandParameter("responseIndex", "The response's number in the list.", ParameterType.INTEGER)
										.setExample("3")
						)
						.build(),
				CommandBuilder.Create("list", "Display all of the current responses.")
						.addAlias("l")
						.addParameters(
								triggerType
										.setExample("random"),
								new CommandParameter("page", "The page to navigate to.", ParameterType.INTEGER, false)
										.setExample("2")
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
			case "list":
				return cmdList(lang, brain, executingCommand);
		}

		return null;
	}

	private EmbedBuilder cmdAdd(Lang lang, Brain brain, ExecutingCommandArguments args) {
		String responseType = args.getAsString("triggerType");
		String responseText = args.getAsString("responseText");

		brain.getTriggerResponses().add(new com.hadenwatne.shmames.models.Response(TriggerType.byName(responseType), responseText));

		return response(EmbedType.SUCCESS)
				.setDescription(lang.getMsg(Langs.ITEM_ADDED));
	}

	private EmbedBuilder cmdDrop(Lang lang, Brain brain, ExecutingCommandArguments args) {
		String responseType = args.getAsString("triggerType");
		int responseIndex = args.getAsInteger("responseIndex");

		List<com.hadenwatne.shmames.models.Response> responses = brain.getResponsesFor(TriggerType.byName(responseType));

		if(responses.size() >= responseIndex) {
			com.hadenwatne.shmames.models.Response r = responses.get(responseIndex-1);
			brain.removeTriggerResponse(r);

			return response(EmbedType.SUCCESS)
					.setDescription(lang.getMsg(Langs.ITEM_REMOVED, new String[]{ r.getResponse() }));
		}else {
			return response(EmbedType.ERROR, Errors.NOT_FOUND.name())
					.setDescription(lang.getError(Errors.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdList(Lang lang, Brain brain, ExecutingCommand executingCommand) {
		String responseType = executingCommand.getCommandArguments().getAsString("triggerType").toUpperCase();
		int page = executingCommand.getCommandArguments().getAsInteger("page");
		final String cacheKey = CacheService.GenerateCacheKey(executingCommand.getServer().getIdLong(), executingCommand.getChannel().getIdLong(), executingCommand.getAuthorUser().getIdLong(), "response-list", responseType);
		final PaginatedList cachedList = CacheService.RetrieveItem(cacheKey, PaginatedList.class);

		PaginatedList paginatedList;

		// If this list has been cached, retrieve it instead of building another one.
		if (cachedList != null) {
			paginatedList = cachedList;
		} else {
			List<com.hadenwatne.shmames.models.Response> responses = brain.getResponsesFor(TriggerType.byName(responseType));

			if(responses.size() == 0) {
				return response(EmbedType.ERROR, Errors.ITEMS_NOT_FOUND.name())
						.setDescription(lang.getError(Errors.ITEMS_NOT_FOUND));
			}

			List<String> responseTexts = new ArrayList<>();

			for(com.hadenwatne.shmames.models.Response response : responses) {
				responseTexts.add(response.getResponse());
			}

			paginatedList = PaginationService.GetPaginatedList(responseTexts, 10, 100, true);

			// Cache the list in case the user continues to call this command for other pages
			CacheService.StoreItem(cacheKey, paginatedList);
		}

		return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), responseType + " Responses", Color.ORANGE, lang);
	}
}
