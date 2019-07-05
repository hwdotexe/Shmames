package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Utils;

public class Enhance implements ICommand {
	@Override
	public String getDescription() {
		return "Enhance things.";
	}
	
	@Override
	public String getUsage() {
		return "enhance";
	}

	@Override
	public String run(String args, User author, Message message) {
		String[] answers = new String[] {"Done - it is now solid gold.", "Done - it now smells nice.",
				"Done - it is now 10GP richer.", "Done - it won a Nobel Prize.", "Done - it now has friends.",
				"Done - it just made the newspaper", "Done - it is now part Dragon", "Done - it now owns the One Ring",
				"Done - it is now a wizard, Harry.", "Done - it came back from the dead.", "Done - it is now a weeb.",
				"Done - it just won the lottery.", "Done - it now plays Minecraft.", "Done - it can now rap mad rhymes.",
				"Done - its ex lover just moved to Madagascar.", "Done - it is now good at archery.", "Done - it can now cast magic.",
				"Done - it now has a college degree", "Done - it just invented the lightsaber.",
				"Done - it is now radioactive."};
		
		return answers[Utils.getRandom(answers.length)];
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
