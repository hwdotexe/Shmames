package tech.hadenw.shmamesbot.commands;

import java.util.Date;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Poll;
import tech.hadenw.shmamesbot.PollTask;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.Brain;

public class Closepoll implements ICommand {
	@Override
	public String getDescription() {
		return "Close a poll early";
	}
	
	@Override
	public String getUsage() {
		return "closepoll <poll ID>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^\\#?[a-zA-Z0-9]{5}$").matcher(args);
		
		if(m.find()) {
			Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
			
			// Omit any # at the beginning.
			if(args.startsWith("#")) {
				args = args.substring(1);
			}
			
			for(Poll p : b.getActivePolls()) {
				if(p.getID().equalsIgnoreCase(args)) {
					// Start a PollTask that ends early
			        Timer t = new Timer();
					t.schedule(new PollTask(p, message.getGuild().getTextChannelById(p.getChannelID()).retrieveMessageById(p.getMessageID()).complete()), new Date());
					
					return "";
				}
			}
			
			// Not found
			return Errors.NOT_FOUND;
		}else {
			// Regex fail
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"closepoll", "close poll", "endpoll", "end poll"};
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
