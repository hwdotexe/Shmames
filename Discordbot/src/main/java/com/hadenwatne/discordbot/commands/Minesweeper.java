package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Locale;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.MinesweepGame;

import javax.annotation.Nullable;

public class Minesweeper implements ICommand {
	@Override
	public String getDescription() {
		return "Play a game of Minesweeper, using a grid size of 6 through 11.";
	}
	
	@Override
	public String getUsage() {
		return "minesweep <size>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0)
			try {
				int size = Integer.parseInt(args);
				
				if(size >= 6 && size <= 11) {
					return MinesweepGame.BuildNewGame(size);
				}else {
					return "Valid range is 6-11.";
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		
		return "Try `minesweep 6`!";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"minesweep", "msweep"};
	}

	@Override
	public void setRunContext(Locale locale, @Nullable Brain brain) {

	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
