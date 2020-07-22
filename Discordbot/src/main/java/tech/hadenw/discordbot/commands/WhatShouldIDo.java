package tech.hadenw.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Utils;

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
				"confuse someone with dementia", "throw eggs at a flock of birds", "rent library books, and return them all sticky",
				"create a reaction video for YouTube", "invite me to other servers >:}",
				"sell essential oils", "demand to see the manager", "start a Flat Earth rally",
				"uncover the truth behind 9/11", "vaguepost on Instagram for attention",
				"play Madden", "scam impressionable old women out of their retirement funds",
				"get a life", "kick a puppy", "kick a kitten", "start a 37-tweet rant",
				"steal art for Karma", "sell out to EA", "text while driving", "watch YouTube Trending", "write a furry comic",
				"protest public health guidelines", "talk to the hand", "make smalltalk with the sign-spinner",
				"drink questionable chemicals", "throw a prom in the McDonalds Playplace"};
	}
	
	@Override
	public String getDescription() {
		return "Get a randomized, possibly sarcastic suggestion to cure your boredom.";
	}
	
	@Override
	public String getUsage() {
		return "what should i do";
	}

	@Override
	public String run(String args, User author, Message message) {
		return intros[Utils.getRandom(intros.length)]+" "+answers[Utils.getRandom(answers.length)]+"!";
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
