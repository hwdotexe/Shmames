package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.MinesweepGame;

public class Minesweeper implements ICommand {
	@Override
	public String getDescription() {
		return "Play a game of Minesweeper. Usage: `minesweep size`";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0)
			try {
				int size = Integer.parseInt(args);
				
				if(size >= 6 && size <= 11) {
					MinesweepGame ms = new MinesweepGame(size);
					
					return ms.convertToDiscord();
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
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
