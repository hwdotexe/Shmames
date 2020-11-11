package com.hadenwatne.discordbot.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
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
		return "timer <time><d/h/m/s> [description]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher cmd = Pattern.compile("^([\\ddhms]+)(\\s.+)?$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(cmd.find()) {
			Matcher time = Pattern.compile("(\\d{1,3})([dhms])", Pattern.CASE_INSENSITIVE).matcher(cmd.group(1));
			String msg = cmd.group(2) != null ? cmd.group(2).trim() : "";
			int seconds = 0;

			while(time.find()) {
				int multiplier = 1;

				switch(time.group(2).toLowerCase()) {
					case "d":
						multiplier = 86400;
						break;
					case "h":
						multiplier = 3600;
						break;
					case "m":
						multiplier = 60;
						break;
					default:
						break;
				}

				seconds += Integer.parseInt(time.group(1)) * multiplier;
			}

			if(seconds > 0) {
				if(seconds > 7776000) {
					return "Timers must be set for 90 days or sooner.";
				}

				long sLong = (long)seconds;

				// Days
				long f_day = TimeUnit.SECONDS.toDays(sLong);
				sLong -= TimeUnit.DAYS.toSeconds(f_day);

				// Hours
				long f_hour = TimeUnit.SECONDS.toHours(sLong);
				sLong -= TimeUnit.HOURS.toSeconds(f_hour);

				// Minutes
				long f_min = TimeUnit.SECONDS.toMinutes(sLong);
				sLong -= TimeUnit.MINUTES.toSeconds(f_min);

				// Seconds
				long f_sec = sLong;

				JTimerTask t = new JTimerTask(author.getAsMention(), message.getChannel().getIdLong(), seconds, msg);
				String timeMsg = (f_day>0?f_day+"d ":"") + (f_hour>0?f_hour+"h ":"") + (f_min>0?f_min+"m ":"") + (f_sec>0?f_sec+"s":"");

				brain.getTimers().add(t);

				return lang.getMsg(Langs.TIMER_STARTED, new String[]{"**" + timeMsg + "**"});
			}else{
				return lang.wrongUsage(getUsage());
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
