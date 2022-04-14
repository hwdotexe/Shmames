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
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Response implements ICommand {
	private final CommandStructure commandStructure;

	public Response() {
		CommandParameter triggerType = new CommandParameter("triggerType", "The type of trigger to provoke this response", ParameterType.SELECTION);

		for(TriggerType type : TriggerType.values()) {
			triggerType.addSelectionOptions(type.name());
		}

		this.commandStructure = CommandBuilder.Create("response", "Manage bot responses.")
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
		String responseType = args.getAsString("triggerType");
		String responseText = args.getAsString("responseText");

		brain.getTriggerResponses().add(new com.hadenwatne.shmames.models.Response(TriggerType.byName(responseType), responseText));

		messagingChannel.sendMessage(lang.getMsg(Langs.ITEM_ADDED));
	}

	private void cmdDrop(Lang lang, Brain brain, ShmamesCommandMessagingChannel messagingChannel, ShmamesCommandArguments args) {
		String responseType = args.getAsString("triggerType");
		int responseIndex = args.getAsInteger("responseIndex");

		List<com.hadenwatne.shmames.models.Response> responses = brain.getResponsesFor(TriggerType.byName(responseType));

		if(responses.size() >= responseIndex) {
			com.hadenwatne.shmames.models.Response r = responses.get(responseIndex-1);
			brain.removeTriggerResponse(r);

			messagingChannel.sendMessage(lang.getMsg(Langs.ITEM_REMOVED, new String[]{ r.getResponse() }));
		}else {
			messagingChannel.sendMessage(lang.getError(Errors.NOT_FOUND, true));
		}
	}

	private void cmdList(Lang lang, Brain brain, ShmamesCommandMessagingChannel messagingChannel, ShmamesCommandArguments args) {
		String responseType = args.getAsString("triggerType");
		int page = args.getAsInteger("page");

		List<com.hadenwatne.shmames.models.Response> responses = brain.getResponsesFor(TriggerType.byName(responseType));

		if(responses.size() == 0) {
			messagingChannel.sendMessage(lang.getError(Errors.ITEMS_NOT_FOUND, true));
			return;
		}

		List<String> responseTexts = new ArrayList<>();

		for(com.hadenwatne.shmames.models.Response response : responses) {
			responseTexts.add(response.getResponse());
		}

		PaginatedList paginatedList = PaginationService.GetPaginatedList(responseTexts, 10, 100, true);

		messagingChannel.sendMessage(PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), responseType.toUpperCase() + " Responses", Color.ORANGE, lang));
	}
}
