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
		if(Pattern.compile("^[a-zA-Z]{7,9} .{3,}$").matcher(args).matches()) {
			String t = args.substring(0, args.indexOf(" ")).toUpperCase();
			t = t.equals("PLAYING") ? "DEFAULT" : t;
			
			GameType type = GameType.valueOf(t);
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
	
	@Override
	public String sanitize(String i) {
		return i;
	}
}
