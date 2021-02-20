package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.CommandHandler;
import com.hadenwatne.shmames.storage.Brain;
import com.hadenwatne.shmames.storage.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;

public class WhatAreTheOdds implements ICommand {
	private Lang lang;

	@Override
	public String getDescription() {
		return "Get the odds out of 100 of something happening.";
	}
	
	@Override
	public String getUsage() {
		return "whataretheodds [query]";
	}

	@Override
	public String run(String args, User author, Message message) {
		for (ICommand c : CommandHandler.getLoadedCommands()) {
			for (String a : c.getAliases()) {
				if (a.equalsIgnoreCase("roll")) {
					c.setRunContext(lang, null);

					return c.run("1d100", author, message);
				}
			}
		}

		return null;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"whataretheodds", "what are the odds"};
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
