package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.game.MinesweepGame;
import net.dv8tion.jda.api.EmbedBuilder;

public class Minesweeper extends Command {
	public Minesweeper() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("minesweeper", "Play a game of Minesweeper, using a grid size of 6 through 11.")
				.addParameters(
						new CommandParameter("size", "The size of the minefield, 6-11", ParameterType.INTEGER)
								.setExample("7")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		int size = executingCommand.getCommandArguments().getAsInteger("size");

		if (size >= 6 && size <= 11) {
			return response(EmbedType.INFO)
					.setDescription(MinesweepGame.BuildNewGame(size));
		} else {
			return response(EmbedType.ERROR)
					.setDescription("Valid range is 6-11");
		}
	}
}
