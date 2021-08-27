package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.models.MinesweepGame;

public class Minesweeper implements ICommand {
	private final CommandStructure commandStructure;

	public Minesweeper() {
		this.commandStructure = CommandBuilder.Create("minesweeper")
				.addParameters(
						new CommandParameter("size", "The size of the minefield, 6-11", ParameterType.INTEGER)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Play a game of Minesweeper, using a grid size of 6 through 11.";
	}

	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`minesweeper 10`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		int size = data.getArguments().getAsInteger("size");

		if (size >= 6 && size <= 11) {
			return MinesweepGame.BuildNewGame(size);
		} else {
			return "Valid range is 6-11.";
		}
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}
