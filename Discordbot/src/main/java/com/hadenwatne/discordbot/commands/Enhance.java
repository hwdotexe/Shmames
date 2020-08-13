package com.hadenwatne.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Utils;

public class Enhance implements ICommand {
	private String[] answers;
	
	public Enhance() {
		answers = new String[] {"Done - @PH is now solid gold.", "Done - @PH now smells nice.",
				"Done - @PH is now 10GP richer.", "Done - @PH won a Nobel Prize.", "Done - @PH now has friends.",
				"Done - @PH just made the newspaper", "Done - @PH is now part Dragon", "Done - @PH now owns the One Ring",
				"Done - @PH is now a wizard, Harry.", "Done - @PH came back from the dead.", "Done - @PH is now a weeb.",
				"Done - @PH just won the lottery.", "Done - @PH now plays Minecraft.", "Done - @PH can now rap mad rhymes.",
				"Done - @PH's ex lover just moved to Madagascar.", "Done - @PH is now good at archery.", "Done - @PH can now cast magic.",
				"Done - @PH now has a college degree", "Done - @PH just invented the lightsaber.",
				"Done - @PH is now radioactive."};
	}
	
	@Override
	public String getDescription() {
		return "Enhance things.";
	}
	
	@Override
	public String getUsage() {
		return "enhance <item>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0)
			return answers[Utils.getRandom(answers.length)].replace("@PH", args);
		
		return Errors.formatUsage(Errors.WRONG_USAGE, this.getUsage());
	}

	@Override
	public String[] getAliases() {
		return new String[] {"enhance"};
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
