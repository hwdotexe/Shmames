package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class AddStatus implements ICommand {
	@Override
	public String getDescription() {
		return "Add a new status to the random pool. Usage: `addstatus statusType statusText`";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^[a-zA-z]{7,9} [a-zA-Z ]+$").matcher(args).matches()) {
			GameType type = GameType.valueOf(args.substring(0, args.indexOf(" ")).toUpperCase());
			String msg = args.substring(args.indexOf(" "));
			
			Shmames.getBrain().getStatuses().put(msg, type);
			Shmames.getJDA().getPresence().setGame(Game.of(type, msg));
			Shmames.saveBrain();
			
			return ":+1:";
		}else {
			return "Usage: `addstatus <actionType> <status>`";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"addstatus", "add status"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replaceAll("", "").toLowerCase();
	}
}
