package com.hadenwatne.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.discordbot.storage.Locale;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.tasks.JTimerTask;

import javax.annotation.Nullable;

public class Timer implements ICommand {
	private Brain brain;

	@Override
	public String getDescription() {
		return "Start a timer, and "+Shmames.getBotName()+" will alert you when it's ready.";
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
					if(time > 90)
						return "Please constrain your timers to 90 days or less.";

					interval = 1;
					break;
				case 'h':
					if(time > 999)
						return "Please constrain your timers to 999 hours or less.";

					interval = 2;
					break;
				case 's':
					if(time > 999)
						return "Please constrain your timers to 999 seconds or less.";

					interval = 4;
					break;
				default:
					if(time > 999)
						return "Please constrain your timers to 999 minutes or less.";

					interval = 3;
				}

				JTimerTask t = new JTimerTask(author.getAsMention(), message.getChannel().getIdLong(), time, interval, rmsg);

				brain.getTimers().add(t);
				
				return "Started a new :alarm_clock: for **" + time + "** " + (interval == 1 ? "Days" : interval == 2 ? "Hours" : interval == 3 ? "Minutes" : "Seconds");
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
	public void setRunContext(Locale locale, @Nullable Brain brain) {
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
