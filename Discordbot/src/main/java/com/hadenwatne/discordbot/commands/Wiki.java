package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Locale;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Utils;

import javax.annotation.Nullable;

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
	public void setRunContext(Locale locale, @Nullable Brain brain) {

	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
