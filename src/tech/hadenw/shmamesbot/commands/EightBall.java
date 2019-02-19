package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Utils;

public class EightBall implements ICommand {
	@Override
	public String getDescription() {
		return "Shake a Magic 8 Ball and let me see your future.";
	}
	
	@Override
	public String getUsage() {
		return "8ball <your question>";
	}

	@Override
	public String run(String args, User author, Message message) {
		String[] answers = new String[] {"Definitely.", "Without a doubt.","Yes - of course.",
				"You can bet on it.","Most likely.","It's looking good!","Duh.","Signs point to yes.",
				"Why don't you ask me later?",
				"Don't count on it.","My reply is no.","My sources say no.","It's not looking good.","I highly doubt it.",
				"Nope.", "No way.", "That's a negative."};
		
		return answers[Utils.getRandom(answers.length)];
	}

	@Override
	public String[] getAliases() {
		return new String[] {"8ball"};
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
