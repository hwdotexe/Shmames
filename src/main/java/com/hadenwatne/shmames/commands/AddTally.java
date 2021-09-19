package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;

public class AddTally implements ICommand {
	private final CommandStructure commandStructure;

	public AddTally() {
		this.commandStructure = CommandBuilder.Create("addtally", "Increments a tally, or creates one if it doesn't exist.")
				.addAlias("add tally")
				.addAlias("add a tally to")
				.addParameters(
						new CommandParameter("tallyName", "The tally to increment.", ParameterType.STRING)
								.setPattern("[\\w\\d\\s]+")
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`addtally professor_trips_on_hdmi_cord`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String tallyArg = data.getArguments().getAsString("tallyName");
		String tally = tallyArg.trim().replaceAll("\\s", "_").replaceAll("\\W", "").toLowerCase();
		int newTally = brain.getTallies().getOrDefault(tally, 0) + 1;

		brain.getTallies().put(tally, newTally);

		return lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{tally, Integer.toString(newTally)});
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}
}
