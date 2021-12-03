package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.MinesweepGame;
import com.hadenwatne.shmames.models.MiniCactpotGame;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;

public class Cactpot implements ICommand {
	private final CommandStructure commandStructure;

	public Cactpot() {
		this.commandStructure = CommandBuilder.Create("cactpot", "Play a game of Mini Cactpot from Final Fantasy XIV.")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`cactpot`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		return MiniCactpotGame.BuildNewGame();
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}
