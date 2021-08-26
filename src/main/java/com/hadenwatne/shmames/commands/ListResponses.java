package com.hadenwatne.shmames.commands;

import java.util.ArrayList;
import java.util.List;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.models.Response;

public class ListResponses implements ICommand {
	private final CommandStructure commandStructure;

	public ListResponses() {
		CommandParameter responseType = new CommandParameter("responseType", "The type of response to list.", ParameterType.SELECTION);

		for (TriggerType type : TriggerType.values()) {
			responseType.addSelectionOptions(type.name());
		}

		this.commandStructure = CommandBuilder.Create("listresponses")
				.addAlias("list responses")
				.addParameters(
						responseType
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Displays the list of random responses for the specified trigger type.";
	}
	
	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`listresponses RANDOM`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		String rType = data.getArguments().getAsString("responseType");

		if(TriggerType.byName(rType) != null) {
			StringBuilder sb = new StringBuilder();

			sb.append("**");
			sb.append(rType.toUpperCase());
			sb.append(" Responses:**\n");

			List<Response> rs = brain.getResponsesFor(TriggerType.byName(rType));
			List<String> rsText = new ArrayList<String>();

			for(Response r : rs){
				rsText.add(r.getResponse());
			}

			String list = Utils.generateList(rsText, -1, true, true);

			if(list.length() == 0)
				sb.append(lang.getError(Errors.ITEMS_NOT_FOUND, true));
			else
				sb.append(list);

			return sb.toString();
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
