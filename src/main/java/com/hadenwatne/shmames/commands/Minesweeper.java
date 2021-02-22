package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.ShmamesLogger;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.models.MinesweepGame;

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
				ShmamesLogger.logException(e);
			}
		
		return "Try `minesweep 6`!";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"minesweep", "msweep"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {

	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
