package com.hadenwatne.shmames.commands;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.Response;
import com.hadenwatne.shmames.services.PaginationService;

public class ListResponses implements ICommand {
	private final CommandStructure commandStructure;

	public ListResponses() {
		CommandParameter responseType = new CommandParameter("responseType", "The type of response to list.", ParameterType.SELECTION);

		for (TriggerType type : TriggerType.values()) {
			responseType.addSelectionOptions(type.name());
		}

		this.commandStructure = CommandBuilder.Create("listresponses", "Displays the list of random responses for the specified trigger type.")
				.addAlias("list responses")
				.addParameters(
						responseType,
						new CommandParameter("page", "The page to navigate to.", ParameterType.INTEGER, false)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`listresponses RANDOM`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		String rType = data.getArguments().getAsString("responseType");
		int page = data.getArguments().getAsInteger("page");

		if(TriggerType.byName(rType) != null) {
			List<Response> responses = brain.getResponsesFor(TriggerType.byName(rType));

			if(responses.size() == 0) {
				return lang.getError(Errors.ITEMS_NOT_FOUND, true);
			}

			List<String> responseTexts = new ArrayList<>();

			for(Response response : responses) {
				responseTexts.add(response.getResponse());
			}

			PaginatedList paginatedList = PaginationService.GetPaginatedList(responseTexts, 10, 75, true);

			data.getMessagingChannel().sendMessage(PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), rType.toUpperCase() + " Responses", Color.ORANGE, lang));

			return "";
		} else {
			StringBuilder types = new StringBuilder();

			for (TriggerType t : TriggerType.values()) {
				if(types.length() > 0)
					types.append(", ");

				types.append("`");
				types.append(t.name());
				types.append("`");
			}

			return lang.getMsg(Langs.INVALID_TRIGGER_TYPE, new String[] { types.toString() });
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
