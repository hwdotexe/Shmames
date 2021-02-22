package com.hadenwatne.shmames.commands;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.tasks.JTimerTask;

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
			int seconds = Utils.convertTimeStringToSeconds(cmd.group(1));
			String msg = cmd.group(2) != null ? cmd.group(2).trim() : "";

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

				JTimerTask t = new JTimerTask(author.getAsMention(), message.getChannel().getIdLong(), message.getIdLong(), seconds, msg);
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
