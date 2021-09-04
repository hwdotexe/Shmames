package com.hadenwatne.shmames.commands;

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

public class AddTrigger implements ICommand {
	private final CommandStructure commandStructure;

	public AddTrigger() {
		CommandParameter triggerType = new CommandParameter("triggerType", "The type of trigger to add", ParameterType.SELECTION);

		for (TriggerType type : TriggerType.values()) {
			triggerType.addSelectionOptions(type.name());
		}

		this.commandStructure = CommandBuilder.Create("addtrigger", "Creates a new trigger word or phrase, which then sends a response for the given type.")
				.addAlias("add trigger")
				.addParameters(
						triggerType,
						new CommandParameter("triggerWord", "The trigger word to use", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`addtrigger RANDOM explosion`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String nttype = data.getArguments().getAsString("triggerType");
		String newtrigger = data.getArguments().getAsString("triggerWord");

		if (!brain.getTriggers().containsKey(newtrigger)) {
			if (TriggerType.byName(nttype) != null) {
				brain.getTriggers().put(newtrigger, TriggerType.byName(nttype));

				return lang.getMsg(Langs.ADD_TRIGGER_SUCCESS, new String[]{nttype, newtrigger});
			} else {
				StringBuilder types = new StringBuilder();

				for (TriggerType t : TriggerType.values()) {
					if (types.length() > 0)
						types.append(", ");

					types.append("`").append(t.name()).append("`");
				}

				return lang.getMsg(Langs.INVALID_TRIGGER_TYPE, new String[]{types.toString()});
			}
		} else {
			return lang.getError(Errors.ALREADY_EXISTS, true);
		}
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}
}
