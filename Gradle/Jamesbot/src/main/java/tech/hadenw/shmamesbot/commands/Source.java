package tech.hadenw.shmamesbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Utils;

public class Source implements ICommand {
	@Override
	public String getDescription() {
		return "Reverse image search based on an image URL.";
	}
	
	@Override
	public String getUsage() {
		return "source <link to image>";
	}

	@Override
	public String run(String args, User author, Message message) {
		// TODO pattern match an image URL or carats (^) to an embedded image
		Matcher m = Pattern.compile("^https?:\\/\\/.+\\.[a-zA-Z]{3,4}$").matcher(args);
		
		if(m.find()) {
			// let the user know we're on the case
			message.getChannel().sendMessage("Searching...").complete();
			
			String r = Utils.getReverseImage(args);
			
			return r;
		}else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"source"};
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
