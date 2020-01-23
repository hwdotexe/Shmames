package tech.hadenw.shmamesbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.JTimerTask;

public class Timer implements ICommand {
	@Override
	public String getDescription() {
		return "Start a timer.";
	}
	
	@Override
	public String getUsage() {
		return "timer <time>[d/h/m/s] [description]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(\\d{1,3})([dhms]?)(.*?)$").matcher(args);
		
		if(m.find()) {
			int time = Integer.parseInt(m.group(1));
			
			String rmsg = m.group(3) != null ? m.group(3) : "";
			
			if(time > 0) {
				int interval = -1;
				String i = m.group(2);
				
				if(i == null || i.length() == 0)
					i = "m";
					
				switch(i.charAt(0)) {
				case 'd':
					interval = 1;
					break;
				case 'h':
					interval = 2;
					break;
				case 'm':
					interval = 3;
					break;
				case 's':
					interval = 4;
					break;
				default:
					interval = 3;
				}
				
				new JTimerTask(author, message.getChannel(), time, interval, rmsg);
				
				return "Started a new :alarm_clock: for **"+time+"** "+(interval==1?"Days":interval==2?"Hours":interval==3?"Minutes":interval==4?"Seconds":"");
			}else {
				return "Please include an actual amount of time!";
			}
		}else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"timer", "remind me in", "alert"};
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
