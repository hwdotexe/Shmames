package com.hadenwatne.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.discordbot.storage.Lang;
import com.hadenwatne.discordbot.storage.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.storage.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.tasks.JTimerTask;

import javax.annotation.Nullable;

public class Timer implements ICommand {
	private Brain brain;
	private Lang lang;

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
		Matcher m = Pattern.compile("^(\\d{1,3})([dhms]?)(.*?)$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(m.find()) {
			int time = Integer.parseInt(m.group(1));
			
			String rmsg = m.group(3) != null ? m.group(3) : "";
			
			if(time > 0) {
				int interval = -1;
				String i = m.group(2).toLowerCase();
				
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

				return lang.getMsg(Langs.TIMER_STARTED, new String[]{ "**" + time + "** " + (interval == 1 ? "Days" : interval == 2 ? "Hours" : interval == 3 ? "Minutes" : "Seconds") });
			} else {
				return lang.getError(Errors.INCOMPLETE, true);
			}
		}else {
			return lang.wrongUsage(getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"timer", "remind me in", "alert"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.brain = brain;
		this.lang = lang;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
