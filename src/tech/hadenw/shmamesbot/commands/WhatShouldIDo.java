package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Utils;

public class WhatShouldIDo implements ICommand {
	private String[] intros;
	private String[] answers;
	
	public WhatShouldIDo() {
		intros = new String[] {"I think you should", "I'd love it if you", "My advice is to", "Hmm, perhaps try to",
				"I know! You should"};
		answers = new String[] {"defile a grave", "rob a candy store", "deface a subway", "steal a baby's candy",
				"pirate a low-budget film", "start a riot about gas prices", "rewatch the Star Wars sequels",
				"curse at an old woman", "donate to a shady charity in Saudi Arabia",
				"prank call insurance companies", "sell drugs to minors", "write a program in PHP",
				"narrate an adult audiobook", "swap jobs with Mike Rowe", "start a riot about waiting in traffic",
				"confuse someone with dimentia", "throw eggs at a flock of birds", "rent library books, and return them all sticky",
				"create a reaction video for YouTube", "invite me to other servers >:}"};
	}
	
	@Override
	public String getDescription() {
		return "I give you quality suggestions.";
	}
	
	@Override
	public String getUsage() {
		return "what should i do";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0)
			return intros[Utils.getRandom(intros.length)]+" "+answers[Utils.getRandom(answers.length)]+"!";
		
		return Errors.formatUsage(Errors.WRONG_USAGE, this.getUsage());
	}

	@Override
	public String[] getAliases() {
		return new String[] {"what should I do"};
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
