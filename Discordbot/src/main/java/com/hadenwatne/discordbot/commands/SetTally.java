package com.hadenwatne.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;

public class SetTally implements ICommand {
	@Override
	public String getDescription() {
		return "Overrides a tally with a new value, creating it if it didn't already exist.";
	}
	
	@Override
	public String getUsage() {
		return "settally <tallyname> <count>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(.+) (\\d{1,3})$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(m.find()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			String tally = m.group(1).replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
			int count = Integer.parseInt(m.group(2));
			
			if (b.getTallies().containsKey(tally)) {
				if(count == 0) {
					b.getTallies().remove(tally);
					
					return "`" + tally + "` hast been removed, sire";
				}
			}
			
			b.getTallies().put(tally, count);
	
			return "Current tally for `" + tally + "`: `"+ count + "`";
		} else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"settally", "set tally"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
