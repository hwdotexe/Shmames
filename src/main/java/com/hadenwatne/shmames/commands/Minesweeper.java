package com.hadenwatne.shmames.commands;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.models.game.MinesweepGame;
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
	protected Permission[] configureRequiredUserPermissions() {
		return new Permission[0];
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("minesweeper", "Play a game of Minesweeper, using a grid size of 6 through 11.")
				.addParameters(
						new CommandParameter("difficulty", "How hard the puzzle should be", ParameterType.SELECTION)
								.setExample("NORMAL")
								.addSelectionOptions("EASY, NORMAL, HARD")
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	public void run(Execution execution) {
		String difficulty = execution.getArguments().get("difficulty").getAsString();
		String game = switch (difficulty.toUpperCase()) {
			case "EASY" -> MinesweepGame.BuildNewGame(6);
			case "HARD" -> MinesweepGame.BuildNewGame(11);
			default -> MinesweepGame.BuildNewGame(8);
		};

		CorvusBuilder builder = Corvus.info(execution.getBot());

		builder.addBreadcrumbs(this.getCommandStructure().getName(), difficulty.toLowerCase())
				.setDescription(game);

		Corvus.reply(execution, builder);
	}
}