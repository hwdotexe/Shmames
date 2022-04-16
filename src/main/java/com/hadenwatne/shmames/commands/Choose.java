package com.hadenwatne.shmames.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.RandomService;
import com.hadenwatne.shmames.enums.Errors;
import net.dv8tion.jda.api.EmbedBuilder;

public class Choose extends Command {
	public Choose() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("choose", "Let me make a decision for you.")
				.addParameters(
						new CommandParameter("thisOrThat", "Two options, separated by 'or'.", ParameterType.STRING)
								.setPattern("(.{1,}) or (.{1,})")
								.setExample("Go outside or One more level")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		Pattern p = data.getCommand().getCommandStructure().getParameters().get(0).getPattern();
		Matcher m = p.matcher(data.getArguments().getAsString());
		String thisOrThat = data.getArguments().getAsString("thisOrThat");

		// The command is already validated. Call m.find() to prepare a new matcher and separate out the arguments.
		m.find();

		int mutator = RandomService.GetRandom(75);
		String response;

		if (mutator < 5) {
			response = "Neither";
		} else if (mutator < 10) {
			response = "Both";
		} else {
			// Starting at group 2 base because the pattern is wrapped inside a named group.
			response = m.group(2 + RandomService.GetRandom(2));
		}

		String choice = lang.getMsg(Langs.CHOOSE, new String[]{response});

		return response(EmbedType.INFO)
				.addField(null, thisOrThat, false)
				.addField(null, choice, false);
	}
}
