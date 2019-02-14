package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class RemoveTrigger implements ICommand {
	@Override
	public String getDescription() {
		return "Removes an existing trigger from the bot. Usage: `removetrigger triggerWord`";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0) {
			if (!args.equalsIgnoreCase("hey james")) {
				if (Shmames.getBrain().getTriggers().containsKey(args)) {
					Shmames.getBrain().getTriggers().remove(args);
					Shmames.saveBrain();
	
					return "I threw it on the **GROUND**!";
				} else
					return "While I'd love to do that, it doesn't exist in my brain, so, I can't...";
			} else {
				return "Sorry, ya can't delete that trigger. It's stuck here like the gum on my shoe #ThanksCarly";
			}
		}else {
			return "Hey! You gotta tell me what you want to delete...";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"removetrigger", "remove trigger"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.toLowerCase();
	}
}
