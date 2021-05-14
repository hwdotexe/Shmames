package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Utils;

import javax.annotation.Nullable;

public class EightBall implements ICommand {
	private String[] replies;
	
	public EightBall() {
		replies = new String[] {"Definitely.", "Without a doubt.","Yes - of course.",
				"You can bet on it.","Most likely.","It's looking good!","Duh.","Signs point to yes.",
				"Why don't you ask me later?",
				"Don't count on it.","My reply is no.","My sources say no.","It's not looking good.","I highly doubt it.",
				"Nope.", "No way.", "That's a negative."};
	}
	
	@Override
	public String getDescription() {
		return "Shake a Magic 8 Ball and let me see your future.";
	}
	
	@Override
	public String getUsage() {
		return "8ball <your question>";
	}

	@Override
	public String getExamples() {
		return "`8ball Am I a pretty girl?`";
	}

	@Override
	public String run(String args, User author, Message message) {
		return replies[Utils.getRandom(replies.length)];
	}

	@Override
	public String[] getAliases() {
		return new String[] {"8ball"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {

	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
