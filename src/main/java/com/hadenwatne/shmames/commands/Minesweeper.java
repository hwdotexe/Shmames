package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.game.MinesweepGame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class Minesweeper extends Command {
	public Minesweeper() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
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
