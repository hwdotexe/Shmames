package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.JTimerTask;

public class JTimer implements ICommand {
	@Override
	public String getDescription() {
		return "Start a timer.";
	}
	
	@Override
	public String getUsage() {
		return "timer <minutes>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^\\d{1,2}$").matcher(args).matches()) {
			int minutes = Integer.parseInt(args);
			
			new JTimerTask(author, message.getChannel(), minutes);
			
			return "Started a new :alarm_clock: for **"+minutes+"** minutes.";
		}else {
			return Errors.WRONG_USAGE;
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"timer"};
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
