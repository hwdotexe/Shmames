package com.hadenwatne.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Utils;

public class Wiki implements ICommand {
	@Override
	public String getDescription() {
		return "Ask the oracle your question, and I shall answer. That, or the Internet will.";
	}
	
	@Override
	public String getUsage() {
		return "wiki <short question>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0)
			return Utils.getWolfram(args);
		else {
			return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"wiki"};
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