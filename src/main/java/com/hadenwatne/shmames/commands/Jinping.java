package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.tasks.JinpingTask;

public class Jinping implements ICommand {
	private final CommandStructure commandStructure;

	public Jinping() {
		this.commandStructure = CommandBuilder.Create("jinping", "Spam :ping_pong: for one minute in support of the Hong Kong pro-democracy protesters.")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		new JinpingTask(brain);
		
		return "SPAM :ping_pong: THIS :ping_pong: PONG :ping_pong: TO :ping_pong: FREE :ping_pong: HONG :ping_pong: KONG";
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
