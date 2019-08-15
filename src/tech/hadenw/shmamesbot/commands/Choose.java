package tech.hadenw.shmamesbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Utils;

public class Choose implements ICommand {
	
	@Override
	public String getDescription() {
		return "Let me make a decision for you.";
	}
	
	@Override
	public String getUsage() {
		return "choose <item> or <item>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(.{1,}) or (.{1,})$").matcher(args);
		
		if(m.find()) {
			int choice = 1 + Utils.getRandom(2);
			String c = m.group(choice);
			
			return "I choose: "+c;
		}else {
			return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"choose"};
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
