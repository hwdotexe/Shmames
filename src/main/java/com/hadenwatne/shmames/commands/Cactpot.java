package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.game.MiniCactpotGame;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class Cactpot extends Command {
	public Cactpot() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
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
