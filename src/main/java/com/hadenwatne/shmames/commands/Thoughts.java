package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.Utils;

public class Thoughts implements ICommand {
	private final CommandStructure commandStructure;
	private final String[] answers = new String[]{"That's incredible!", "I love it.", "The best thing all week.", "YAAS QUEEN", "Amazing!",
			"Fantastic :ok_hand:", "I am indifferent.", "Could be better.", "Ick, no way!", "Just no.", "That is offensive.",
			"I hate that.", "Get that garbage out of my face!"};

	public Thoughts() {
		this.commandStructure = CommandBuilder.Create("thoughts", "Get my randomized opinion on something.")
				.addAlias("what do you think about")
				.addAlias("what do you think of")
				.addParameters(
						new CommandParameter("item", "The item to get my thoughts about", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`thoughts The State of Ohio`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		return answers[Utils.getRandom(answers.length)];
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}
