package com.hadenwatne.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;

public class AddTally implements ICommand {
	@Override
	public String getDescription() {
		return "Increments a tally, or creates one if it doesn't exist.";
	}
	
	@Override
	public String getUsage() {
		return "addtally <tallyname>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		if (b.getTallies().containsKey(args)) {
			b.getTallies().put(args, b.getTallies().get(args) + 1);
		} else {
			b.getTallies().put(args, 1);
		}

		return "Current tally for `" + args + "`: `"+ b.getTallies().get(args) + "`";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addtally", "add tally", "add a tally to"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
