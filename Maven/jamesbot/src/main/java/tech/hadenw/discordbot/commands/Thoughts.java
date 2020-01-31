package tech.hadenw.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Utils;

public class Thoughts implements ICommand {
	private String[] answers;
	
	public Thoughts() {
		answers = new String[] {"That's incredible!", "I love it.", "The best thing all week.", "YAAS QUEEN", "Amazing!", 
				"Fantastic :ok_hand:", "I am indifferent.", "Could be better.", "Ick, no way!", "Just no.", "That is offensive.",
				"I hate that.", "Get that garbage out of my face!"};
	}
	
	@Override
	public String getDescription() {
		return "Get my opinion.";
	}
	
	@Override
	public String getUsage() {
		return "thoughts <item>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0)
			return answers[Utils.getRandom(answers.length)];
		
		return Errors.formatUsage(Errors.WRONG_USAGE, this.getUsage());
	}

	@Override
	public String[] getAliases() {
		return new String[] {"thoughts", "what do you think about"};
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
