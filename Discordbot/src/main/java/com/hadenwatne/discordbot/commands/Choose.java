package com.hadenwatne.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Utils;

public class Choose implements ICommand {
	
	@Override
	public String getDescription() {
		return "Let me make a decision for you.";
	}
	
	@Override
	public String getUsage() {
		return "choose <item> or <item>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(.{1,}) or (.{1,})$").matcher(args);
		
		if(m.find()) {
			int mutator = Utils.getRandom(50);
			
			if(mutator < 5) { // 10%
				return "I choose: Neither!";
			} else if(mutator < 10) { // 20%
				return "I choose: Both!";
			} else {
				String c = m.group(1 + Utils.getRandom(2));
				
				return "I choose: "+c;
			}
		}else {
			return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"choose"};
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
