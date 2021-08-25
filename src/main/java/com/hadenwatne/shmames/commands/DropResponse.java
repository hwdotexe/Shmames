package com.hadenwatne.shmames.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.Response;

import javax.annotation.Nullable;

public class DropResponse implements ICommand {
	private final CommandStructure commandStructure;

	public DropResponse() {
		CommandParameter triggerType = new CommandParameter("triggerType", "The type of trigger this response has", ParameterType.SELECTION);

		for(TriggerType type : TriggerType.values()) {
			triggerType.addSelectionOptions(type.name());
		}

		this.commandStructure = CommandBuilder.Create("dropresponse")
				.addAlias("drop response")
				.addParameters(
						triggerType,
						new CommandParameter("responseIndex", "The response's number in the list", ParameterType.INTEGER)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Removes an existing response from the list for the specified type. Use the `listResponses` command " +
				"to view response numbers.";
	}
	
	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`dropresponse RANDOM 2`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		String nrtype = data.getArguments().getAsString("triggerType");
		int rNum = data.getArguments().getAsInteger("responseIndex");

		if (TriggerType.byName(nrtype) != null) {
			List<Response> responses = brain.getResponsesFor(TriggerType.byName(nrtype));

			if(responses.size() >= rNum) {
				Response r = responses.get(rNum-1);
				brain.removeTriggerResponse(r);

				return lang.getMsg(Langs.ITEM_REMOVED, new String[]{ r.getResponse() });
			}else {
				return lang.getError(Errors.NOT_FOUND, true);
			}
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
