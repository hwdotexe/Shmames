package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class SetStatus implements ICommand {
	@Override
	public String getDescription() {
		return "Sets a temporary status for the bot. Usage: `setstatus statusType statusText`";
	}

	@Override
	public String run(String args, User author, Guild server) {
		if(Pattern.compile("^[a-zA-z]{7,9} [a-zA-Z ]+$").matcher(args).matches()) {
			GameType type = GameType.valueOf(args.substring(0, args.indexOf(" ")).toUpperCase());
			String message = args.substring(args.indexOf(" "));
			
			Shmames.getJDA().getPresence().setGame(Game.of(type, message));
			
			return ":v:";
		}else {
			return "Usage: `setstatus <actionType> <status>`";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"setstatus", "set status"};
	}
}
