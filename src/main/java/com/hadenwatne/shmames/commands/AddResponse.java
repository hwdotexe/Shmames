package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.*;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.enums.TriggerType;

public class AddResponse implements ICommand {
	private final CommandStructure commandStructure;

	public AddResponse() {
		CommandParameter triggerType = new CommandParameter("triggerType", "The type of trigger to provoke this response", ParameterType.SELECTION);

		for(TriggerType type : TriggerType.values()) {
			triggerType.addSelectionOptions(type.name());
		}

		this.commandStructure = CommandBuilder.Create("addresponse")
				.addAlias("add response")
				.addParameters(
						triggerType,
						new CommandParameter("responseText", "The actual text of this response", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Adds a new random response for the chosen trigger type.";
	}
	
	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`addresponse RANDOM Your mother was a hamster!`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		String nrtype = data.getArguments().getAsString("triggerType");
		String newresp = data.getArguments().getAsString("responseText");

		// Safety check
		if (TriggerType.byName(nrtype) != null) {
			brain.getTriggerResponses().add(new Response(TriggerType.byName(nrtype), newresp));

			return lang.getMsg(Langs.ITEM_ADDED);
		} else {
			StringBuilder types = new StringBuilder();

			for (TriggerType t : TriggerType.values()) {
				if(types.length() > 0)
					types.append(", ");

				types.append("`").append(t.name()).append("`");
			}

			return lang.getMsg(Langs.INVALID_TRIGGER_TYPE, new String[] { types.toString() });
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
