package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.Shmames;

import javax.annotation.Nullable;

public class DropTrigger implements ICommand {
	private final CommandStructure commandStructure;

	public DropTrigger() {
		this.commandStructure = CommandBuilder.Create("droptrigger", "Removes an existing trigger word or phrase.")
				.addAlias("drop trigger")
				.addParameters(
						new CommandParameter("triggerWord", "The trigger to remove.", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`droptrigger explosion`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		String triggerWord = data.getArguments().getAsString("triggerWord");

		if(triggerWord.equalsIgnoreCase(Shmames.getBotName())) {
			return lang.getError(Errors.CANNOT_DELETE, true);
		}

		if (brain.getTriggers().containsKey(triggerWord)) {
			brain.getTriggers().remove(triggerWord);

			return lang.getMsg(Langs.ITEM_REMOVED, new String[]{ triggerWord });
		} else {
			return lang.getError(Errors.NOT_FOUND, true);
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
