package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.TimeoutTask;

public class Timeout implements ICommand {
	@Override
	public String getDescription() {
		return "Put the bot on time-out.";
	}
	
	@Override
	public String getUsage() {
		return "timeout";
	}

	@Override
	public String run(String args, User author, Message message) {
		new TimeoutTask();
		return "";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"timeout"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
