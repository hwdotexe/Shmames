package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.PaginatedList;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.PaginationService;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ListTriggers implements ICommand {
	private final CommandStructure commandStructure;

	public ListTriggers() {
		this.commandStructure = CommandBuilder.Create("listtriggers", "Displays all the current message trigger words or phrases, along with their types.")
				.addAlias("list triggers")
				.addParameters(
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
		return "`listtriggers`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		int page = data.getArguments().getAsInteger("page");
		HashMap<String, TriggerType> triggers = brain.getTriggers();
		List<String> triggersFormatted = formatTriggersToStringList(triggers);

		PaginatedList paginatedList = PaginationService.GetPaginatedList(triggersFormatted, 15, -1, false);

		data.getMessagingChannel().sendMessage(PaginationService.DrawEmbedPage(paginatedList, Math.max(1, page), lang.getMsg(Langs.TRIGGER_LIST), Color.ORANGE, lang));

		return "";
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}

	private List<String> formatTriggersToStringList(HashMap<String, TriggerType> triggers) {
		List<String> triggerList = new ArrayList<>();

		for(String key : triggers.keySet()) {
			triggerList.add(key + ": **" + triggers.get(key).name() + "**");
		}

		return triggerList;
	}
}
