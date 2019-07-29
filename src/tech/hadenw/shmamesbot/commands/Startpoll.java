package tech.hadenw.shmamesbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Poll;
import tech.hadenw.shmamesbot.Shmames;

public class Startpoll implements ICommand {
	@Override
	public String getDescription() {
		return "Starts a new poll in the current channel.";
	}
	
	@Override
	public String getUsage() {
		return "startpoll <time>[d/h/m/s] Question? OptionA; OptionB...";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(\\d{1,3})([dhms])? (.*\\?) ((.+); (.+))$").matcher(args);
		
		if(m.find()) {
			int time = Integer.parseInt(m.group(1));
			String interval = m.group(2); // Could be empty!
			String question = m.group(3);
			String opt = m.group(4);
			
			// Bugfix: replace channel names within the question.
			Matcher bf = Pattern.compile("<#(\\d{5,})>").matcher(question);
			
			if(bf.find()) {
				question = bf.replaceFirst("#"+message.getGuild().getTextChannelById(bf.group(1)).getName());
			}
			// End bugfix
			
			List<String> options = new ArrayList<String>();
			
			for(String s : opt.split(";")) {
				options.add(s.trim());
			}
			
			if(options.size() > 1 && options.size() <= 9) {
				try {
					message.delete().queue();
				}catch(Exception e) {
					// Do nothing; we don't have permission
				}
				
				Shmames.getPolls().add(new Poll(message.getChannel(), question, options, time, interval));
			}else {
				return "You must provide at least 2 different options, and fewer than 9!";
			}
		}else {
			// Regex fail
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
		
		return "";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"startpoll", "start poll"};
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
