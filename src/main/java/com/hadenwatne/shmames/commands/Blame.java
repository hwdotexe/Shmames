package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Utils;

import javax.annotation.Nullable;

public class Blame implements ICommand {
	private final CommandStructure commandStructure;
	private final String[] answers = new String[] {"Obama", "Trump", "Blizzard", "China", "EA", "4Chan", "your mom",
			"the economy", "Big Pharma", "India", "Nigeria", "Mexico", "Chemtrails", "GMOs",
			"vaccines", "#VapeLife", "weebs", "essential oils", "Karen", "Epic Games",
			"video games", "hip hop", "Fortnite", "Source Filmmaker", "Discord", "Coronavirus", "Apple",
			"Google", "Starbucks", "NASA", "Keanu Reeves", "Oscar the Grouch", "Ohio"};

	public Blame() {
		this.commandStructure = CommandBuilder.Create("blame", "I'll blame stuff for you.")
				.addAlias("why")
				.addParameters(
						new CommandParameter("item", "The item to blame", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`blame cropcircles`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		if(brain != null) {
			if(brain.getJinping()) {
				return lang.getMsg(Langs.BLAME, new String[]{ "Jinping" });
			}
		}

		return lang.getMsg(Langs.BLAME, new String[]{ answers[Utils.getRandom(answers.length)] });
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
