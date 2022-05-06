package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.game.MiniCactpotGame;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;

public class Cactpot extends Command {
	public Cactpot() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("cactpot", "Play a game of Mini Cactpot from Final Fantasy XIV.")
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		return response(EmbedType.INFO)
				.setDescription(MiniCactpotGame.BuildNewGame());
	}
}
