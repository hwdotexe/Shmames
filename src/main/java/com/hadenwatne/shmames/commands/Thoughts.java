package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Utils;

import javax.annotation.Nullable;

public class Thoughts implements ICommand {
	private String[] answers;
	private Lang lang;
	
	public Thoughts() {
		answers = new String[] {"That's incredible!", "I love it.", "The best thing all week.", "YAAS QUEEN", "Amazing!", 
				"Fantastic :ok_hand:", "I am indifferent.", "Could be better.", "Ick, no way!", "Just no.", "That is offensive.",
				"I hate that.", "Get that garbage out of my face!"};
	}
	
	@Override
	public String getDescription() {
		return "Get my randomized opinion on something.";
	}
	
	@Override
	public String getUsage() {
		return "thoughts <item>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0)
			return answers[Utils.getRandom(answers.length)];

		return lang.wrongUsage(getUsage());
	}

	@Override
	public String[] getAliases() {
		return new String[] {"thoughts", "what do you think about"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
