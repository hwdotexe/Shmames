package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.Utils;

// TODO watch this command for uses, and delete if no longer used.
public class Enhance implements ICommand {
	private final CommandStructure commandStructure;
	private String[] answers = new String[]{"Done - @PH is now solid gold.", "Done - @PH now smells nice.",
			"Done - @PH is now 10GP richer.", "Done - @PH won a Nobel Prize.", "Done - @PH now has friends.",
			"Done - @PH just made the newspaper", "Done - @PH is now part Dragon", "Done - @PH now owns the One Ring",
			"Done - @PH is now a wizard, Harry.", "Done - @PH came back from the dead.", "Done - @PH is now a weeb.",
			"Done - @PH just won the lottery.", "Done - @PH now plays Minecraft.", "Done - @PH can now rap mad rhymes.",
			"Done - @PH's ex lover just moved to Madagascar.", "Done - @PH is now good at archery.", "Done - @PH can now cast magic.",
			"Done - @PH now has a college degree", "Done - @PH just invented the lightsaber.",
			"Done - @PH is now radioactive."};

	public Enhance() {
		this.commandStructure = CommandBuilder.Create("enhance", "Enhance things.")
				.addParameters(
						new CommandParameter("thing", "The item you want to enhance.", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`enhance Polka music`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		String item = data.getArguments().getAsString("thing");
		String answer = answers[Utils.getRandom(answers.length)].replace("@PH", item);

		if (data.getMessagingChannel().hasHook()) {
			return "> _Enhance " + item + "_\n" + answer;
		}

		return answer;
	}

	@Override
	public boolean requiresGuild() {
		return false;
	}
}