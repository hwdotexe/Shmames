package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.storage.Lang;
import com.hadenwatne.shmames.storage.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.storage.Brain;

import javax.annotation.Nullable;

public class Blame implements ICommand {
	private String[] answers;
	private Lang lang;
	private @Nullable Brain brain;
	
	public Blame() {
		answers = new String[] {"Obama", "Trump", "Blizzard", "China", "EA", "4Chan", "your mom",
				"the economy", "Big Pharma", "India", "Nigeria", "Mexico", "Chemtrails", "GMOs",
				"vaccines", "#VapeLife", "weebs", "essential oils", "Karen", "Epic Games",
				"video games", "hip hop", "Fortnite", "Source Filmmaker", "Discord", "Coronavirus", "Apple",
				"Google", "Starbucks", "NASA", "Keanu Reeves", "Oscar the Grouch", "Ohio"};
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
				return lang.getMsg(Langs.BLAME, new String[]{ "Jinping" });
			}
		}

		return lang.getMsg(Langs.BLAME, new String[]{ answers[Utils.getRandom(answers.length)] });
	}

	@Override
	public String[] getAliases() {
		return new String[] {"blame", "why"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
