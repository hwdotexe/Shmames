package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.Utils;

public class ListTriggers implements ICommand {
	private final CommandStructure commandStructure;

	public ListTriggers() {
		this.commandStructure = CommandBuilder.Create("listtriggers")
				.addAlias("list triggers")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Displays all the current message trigger words or phrases, along with their types.";
	}
	
	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`listtriggers`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		String list = Utils.generateList(brain.getTriggers(), -1, true);

		return lang.getMsg(Langs.TRIGGER_LIST)+"\n"+list;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
