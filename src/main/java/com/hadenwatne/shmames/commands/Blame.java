package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.EmbedBuilder;

public class Blame extends Command {
	public Blame() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("blame", "I'll blame stuff for you.")
				.addAlias("why")
				.addParameters(
						new CommandParameter("item", "The item to blame", ParameterType.STRING)
								.setExample("cropcircles")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (Lang lang, Brain brain, ShmamesCommandData data) {
		if (brain != null) {
			if (brain.getJinping()) {
				String response = lang.getMsg(Langs.BLAME, new String[]{"Jinping"});

				return response(EmbedType.INFO)
						.addField(null, response, false);
			}
		}

		String response = "";
		String randomAnswer = lang.getMsg(Langs.BLAME_OPTIONS);
		String answerMessage = lang.getMsg(Langs.BLAME, new String[]{randomAnswer});

		if (data.getMessagingChannel().hasHook()) {
			String question = data.getArguments().getAsString("item");

			response = "> _Why " + question + "_\n" + answerMessage;
		} else {
			response = answerMessage;
		}

		return response(EmbedType.INFO)
				.addField(null, response, false);
	}
}
