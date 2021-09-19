package com.hadenwatne.shmames.commands;

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

public class SetTally implements ICommand {
	private final CommandStructure commandStructure;

	public SetTally() {
		this.commandStructure = CommandBuilder.Create("settally", "Overrides a tally with a new value, creating it if it didn't already exist.")
				.addAlias("set tally")
				.addParameters(
						new CommandParameter("tallyName", "The tally to set.", ParameterType.STRING)
								.setPattern("[\\w\\d\\s]+"),
						new CommandParameter("count", "The new count of the tally", ParameterType.INTEGER)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`settally professor_trips_on_hdmi_cord 17`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String tallyName = data.getArguments().getAsString("tallyName");
		tallyName = tallyName.trim().replaceAll("\\s", "_").replaceAll("\\W", "").toLowerCase();
		int tallyCount = data.getArguments().getAsInteger("count");

		if (tallyCount > 0) {
			brain.getTallies().put(tallyName, tallyCount);

			return lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{tallyName, Integer.toString(tallyCount)});
		} else {
			brain.getTallies().remove(tallyName);

			return lang.getMsg(Langs.TALLY_REMOVED, new String[]{tallyName});
		}
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}
}

