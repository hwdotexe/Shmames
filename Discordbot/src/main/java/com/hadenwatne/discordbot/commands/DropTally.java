package com.hadenwatne.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;

public class DropTally implements ICommand {
	@Override
	public String getDescription() {
		return "Decrements a tally, or removes it if the tally reaches 0.";
	}
	
	@Override
	public String getUsage() {
		return "droptally <tallyName>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		
		if (b.getTallies().containsKey(args)) {
			int tallies = b.getTallies().get(args);

			if (tallies - 1 < 1) {
				b.getTallies().remove(args);
				
				return "`" + args + "` hast been removed, sire";
			} else {
				b.getTallies().put(args, tallies - 1);
				
				return "Current tallies for `" + args + "`: `" + b.getTallies().get(args) + "`";
			}
		} else {
			return Errors.NOT_FOUND;
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"droptally", "drop tally", "removetally", "remove tally"};
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
