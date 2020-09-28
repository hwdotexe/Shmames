package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.storage.Errors;
import com.hadenwatne.discordbot.Utils;

import javax.annotation.Nullable;

public class Wiki implements ICommand {
	private Lang lang;

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
			return lang.getError(Errors.INCOMPLETE, true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"wiki"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
