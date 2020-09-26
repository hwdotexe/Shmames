package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Locale;
import com.hadenwatne.discordbot.storage.Locales;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class Blame implements ICommand {
	private String[] answers;
	private Locale locale;
	private @Nullable Brain brain;
	
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
		if(brain != null) {
			if(brain.getJinping()) {
				return locale.getMsg(Locales.BLAME, new String[]{ "Jinping" });
			}
		}

		return locale.getMsg(Locales.BLAME, new String[]{ answers[Utils.getRandom(answers.length)] });
	}

	@Override
	public String[] getAliases() {
		return new String[] {"blame", "why"};
	}

	@Override
	public void setRunContext(Locale locale, @Nullable Brain brain) {
		this.locale = locale;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
