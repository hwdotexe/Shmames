package com.hadenwatne.shmames.commands;

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

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropTally implements ICommand {
	private final CommandStructure commandStructure;

	public DropTally() {
		this.commandStructure = CommandBuilder.Create("droptally", "Decrements a tally, or removes it if the tally reaches 0.")
				.addAlias("drop tally")
				.addAlias("remove a tally from")
				.addParameters(
						new CommandParameter("tallyName", "The tally to decrement.", ParameterType.STRING)
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
		return "`droptally professor_trips_on_hdmi_cord`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String tallyArg = data.getArguments().getAsString("tallyName");
		String tally = tallyArg.trim().replaceAll("\\s", "_").replaceAll("\\W", "").toLowerCase();
		int newTally = brain.getTallies().getOrDefault(tally, 0) - 1;

		if (newTally == -1) {
			// Never existed
			return lang.getError(Errors.NOT_FOUND, true);
		} else if (newTally == 0) {
			// Existed and removed
			brain.getTallies().remove(tally);

			return lang.getMsg(Langs.TALLY_REMOVED, new String[]{tally});
		} else {
			// Exists and lowers by 1
			brain.getTallies().put(tally, newTally);

			return lang.getMsg(Langs.TALLY_CURRENT_VALUE, new String[]{ tally, Integer.toString(newTally) });
		}
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}
}