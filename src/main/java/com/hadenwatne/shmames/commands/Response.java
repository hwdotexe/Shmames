package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.*;
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
import java.util.List;

public class Response extends Command {
	public Response() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		CommandParameter triggerType = new CommandParameter("triggerType", "The type of trigger to provoke this response", ParameterType.SELECTION);
		CommandParameter responseType = new CommandParameter("responseType", "The type of response to send", ParameterType.SELECTION);

		for (TriggerType type : TriggerType.values()) {
			triggerType.addSelectionOptions(type.name());
		}

		for (ResponseType type : ResponseType.values()) {
			responseType.addSelectionOptions(type.name());
		}

		return CommandBuilder.Create("response", "Manage bot responses.")
				.addSubCommands(
						CommandBuilder.Create("add", "Add a new response to the random pool.")
								.addAlias("a")
								.addParameters(
										triggerType
												.setExample("random"),
										responseType
												.setExample("text"),
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
		Language language = executingCommand.getLanguage();
		Brain brain = executingCommand.getBrain();

		switch (subCommand) {
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
		String responseType = args.getAsString("responseType");
		String responseText = args.getAsString("responseText");

		ResponseType rType = responseType == null ? ResponseType.TEXT : ResponseType.valueOf(responseType.toUpperCase());

		brain.getTriggerResponses().add(new com.hadenwatne.shmames.models.Response(TriggerType.byName(triggerType), responseText, rType));

		return response(EmbedType.SUCCESS)
				.setDescription(language.getMsg(LanguageKeys.ITEM_ADDED));
	}

	private EmbedBuilder cmdDrop(Language language, Brain brain, ExecutingCommandArguments args) {
		String triggerType = args.getAsString("triggerType");
		int responseIndex = args.getAsInteger("responseIndex");

		List<com.hadenwatne.shmames.models.Response> responses = brain.getResponsesFor(TriggerType.byName(triggerType));

		if(responses.size() >= responseIndex) {
			com.hadenwatne.shmames.models.Response r = responses.get(responseIndex-1);
			brain.removeTriggerResponse(r);

			return response(EmbedType.SUCCESS)
					.setDescription(language.getMsg(LanguageKeys.ITEM_REMOVED, new String[]{ r.getResponse() }));
		}else {
			return response(EmbedType.ERROR, ErrorKeys.NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.NOT_FOUND));
		}
	}

	private EmbedBuilder cmdList(Language language, Brain brain, ExecutingCommand executingCommand) {
		String triggerType = executingCommand.getCommandArguments().getAsString("triggerType").toUpperCase();
		int page = executingCommand.getCommandArguments().getAsInteger("page");
		final String cacheKey = CacheService.GenerateCacheKey(executingCommand.getServer().getIdLong(), executingCommand.getChannel().getIdLong(), executingCommand.getAuthorUser().getIdLong(), "response-list", triggerType);
		final PaginatedList cachedList = CacheService.RetrieveItem(cacheKey, PaginatedList.class);

		PaginatedList paginatedList;

		// If this list has been cached, retrieve it instead of building another one.
		if (cachedList != null) {
			paginatedList = cachedList;
		} else {
			List<com.hadenwatne.shmames.models.Response> responses = brain.getResponsesFor(TriggerType.byName(triggerType));

			if(responses.size() == 0) {
				return response(EmbedType.ERROR, ErrorKeys.ITEMS_NOT_FOUND.name())
						.setDescription(language.getError(ErrorKeys.ITEMS_NOT_FOUND));
			}

			List<String> responseTexts = new ArrayList<>();

			for(com.hadenwatne.shmames.models.Response response : responses) {
				responseTexts.add("**["+response.getResponseType().name()+"]** " + response.getResponse());
			}

			paginatedList = PaginationService.GetPaginatedList(responseTexts, 10, 100, true);

			// Cache the list in case the user continues to call this command for other pages
			CacheService.StoreItem(cacheKey, paginatedList);
		}

		return PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), triggerType + " Responses", Color.ORANGE, language);
	}
}
