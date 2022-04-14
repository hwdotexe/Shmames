package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;

public class Thoughts implements ICommand {
	private final CommandStructure commandStructure;

	public Thoughts() {
		this.commandStructure = CommandBuilder.Create("thoughts", "Get my randomized opinion on something.")
				.addAlias("what do you think about")
				.addAlias("what do you think of")
				.addParameters(
						new CommandParameter("item", "The item to get my thoughts about", ParameterType.STRING)
								.setExample("my style")
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		return lang.getMsg(Langs.THOUGHTS_OPTIONS);
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}
