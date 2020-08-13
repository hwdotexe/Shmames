package com.hadenwatne.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;

public class DropTrigger implements ICommand {
	@Override
	public String getDescription() {
		return "Removes an existing trigger word or phrase.";
	}
	
	@Override
	public String getUsage() {
		return "removeTrigger <triggerWord>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0) {
			if (!args.equalsIgnoreCase(Shmames.getBotName())) {
				Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
				
				if (b.getTriggers().containsKey(args)) {
					b.getTriggers().remove(args);
	
					return "I threw it on the **GROUND**!";
				} else
					return "While I'd love to do that, it doesn't exist in my brain, so, I can't...";
			} else {
				return "Sorry, ya can't delete that trigger. It's stuck here like the gum on my shoe.";
			}
		}else {
			return "Hey! You gotta tell me what you want to delete...";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"droptrigger", "drop trigger", "remove trigger"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.toLowerCase();
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}