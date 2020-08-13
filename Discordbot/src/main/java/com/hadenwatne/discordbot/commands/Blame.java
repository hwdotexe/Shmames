package com.hadenwatne.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.Brain;

public class Blame implements ICommand {
	private String[] answers;
	
	public Blame() {
		answers = new String[] {"Obama", "Trump", "Blizzard", "China", "EA", "4Chan", "your mom",
				"the economy", "Big Pharma", "India", "Nigeria", "Mexico", "Chemtrails", "GMOs",
				"vaccines", "#VapeLife", "weebs", "essential oils", "Karen", "Epic Games",
				"video games", "hip hop", "Fortnite", "Source Filmmaker", "Discord", "Coronavirus", "Apple",
				"Google", "Starbucks", "NASA", "Keanu Reeves", "Oscar the Grouch"};
	}
	
	@Override
	public String getDescription() {
		return "I'll blame stuff for you.";
	}
	
	@Override
	public String getUsage() {
		return "blame <item>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(message.isFromGuild()){
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

			if(b.getJinping()){
				return "I blame Jinping";
			}
		}

		return "I blame "+answers[Utils.getRandom(answers.length)];
	}

	@Override
	public String[] getAliases() {
		return new String[] {"blame", "why"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
