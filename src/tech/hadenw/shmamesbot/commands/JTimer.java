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
		return "timer <time>[d/h/m/s]";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^\\d{1,3}[dhms]?$").matcher(args).matches()) {
			int time;
			
			System.out.println(args.charAt(args.length()-1));
			
			if(isInt(args.charAt(args.length()-1))){
				time = Integer.parseInt(args);
			}else {
				time = Integer.parseInt(args.substring(0, args.length()-1));
			}
			
			if(time > 0) {
				int interval = -1;
				switch(args.charAt(args.length()-1)) {
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
				
				new JTimerTask(author, message.getChannel(), time, interval);
				
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
	
	private boolean isInt(char c) {
		try {
			Integer.parseInt(String.valueOf(c));
			return true;
		}catch(Exception e) {
			return false;
		}
	}
}
