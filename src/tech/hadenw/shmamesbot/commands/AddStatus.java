package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class AddStatus implements ICommand {
	@Override
	public String getDescription() {
		return "Add a new status to the random pool.";
	}

	@Override
	public String run(String args, User author, Guild server) {
		if(Pattern.compile("^[a-zA-z]{7,9} [a-zA-Z ]+$").matcher(args).matches()) {
			GameType type = GameType.valueOf(args.substring(0, args.indexOf(" ")).toUpperCase());
			String message = args.substring(args.indexOf(" "));
			
			Shmames.getBrain().getStatuses().put(message, type);
			Shmames.getJDA().getPresence().setGame(Game.of(type, message));
			
			return ":+1:";
		}else {
			return "Usage: `addstatus <actionType> <status>`";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addstatus", "add status"};
	}
}
