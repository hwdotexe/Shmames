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
		this.commandStructure = CommandBuilder.Create("minesweeper", "Play a game of Minesweeper, using a grid size of 6 through 11.")
				.addParameters(
						new CommandParameter("size", "The size of the minefield, 6-11", ParameterType.INTEGER)
				)
				.setExample("minesweeper 7")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
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
