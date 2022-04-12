package com.hadenwatne.shmames.commands;

import java.util.LinkedHashMap;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.PaginationService;

public class ListTallies implements ICommand {
	private final CommandStructure commandStructure;

	public ListTallies() {
		this.commandStructure = CommandBuilder.Create("listtallies", "Displays all the current tallies.")
				.addAlias("list tallies")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`listtallies`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		LinkedHashMap<String, Integer> tSorted = DataService.SortHashMap(brain.getTallies());
		
		String tallies = PaginationService.GenerateList(tSorted, -1, true);

		return lang.getMsg(Langs.TALLY_LIST)+"\n"+tallies;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
