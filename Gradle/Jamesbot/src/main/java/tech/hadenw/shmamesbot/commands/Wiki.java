package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Utils;

public class Wiki implements ICommand {
	@Override
	public String getDescription() {
		return "Ask the oracle your question, and I shall answer.";
	}
	
	@Override
	public String getUsage() {
		return "wiki <short question>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0)
			return Utils.getWA(args);
		else {
			return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"wiki"};
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
