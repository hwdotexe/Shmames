package com.hadenwatne.shmames.commands;

import java.util.concurrent.TimeUnit;

import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.tasks.JTimerTask;

public class Timer implements ICommand {
	private final CommandStructure commandStructure;

	public Timer() {
		this.commandStructure = CommandBuilder.Create("timer")
				.addAlias("remind me in")
				.addAlias("alert")
				.addParameters(
						new CommandParameter("duration", "The amount of time before the timer runs", ParameterType.TIMECODE),
						new CommandParameter("message", "The description of the timer", ParameterType.STRING, false)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Start a timer, and "+Shmames.getBotName()+" will alert you when it's ready.";
	}
	
	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`timer 24h Pizza Time`\n" +
				"`timer 15m30s Downvote Horny Bard`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		String duration = data.getArguments().getAsString("duration");
		String msg = data.getArguments().getAsString("message");
		int seconds = Utils.convertTimeStringToSeconds(duration);
		User author = data.getAuthor();
		ShmamesCommandMessagingChannel messagingChannel = data.getMessagingChannel();

		if (seconds > 0) {
			if (seconds > 31536000) {
				return "Timers must be set for 365 days or sooner.";
			}

			long sLong = seconds;

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

			// Schedule the task.
			String messageID = messagingChannel.hasOriginMessage() ? messagingChannel.getOriginMessage().getId() : null;
			JTimerTask t = new JTimerTask(author.getAsMention(), messagingChannel.getChannel().getIdLong(), messageID, seconds, msg);
			String timeMsg = (f_day > 0 ? f_day + "d " : "") + (f_hour > 0 ? f_hour + "h " : "") + (f_min > 0 ? f_min + "m " : "") + (f_sec > 0 ? f_sec + "s" : "");

			brain.getTimers().add(t);

			String resultMessage = lang.getMsg(Langs.TIMER_STARTED, new String[]{"**" + timeMsg + "**"});

			if(messagingChannel.hasHook()) {
				messagingChannel.getHook().setEphemeral(true);
				messagingChannel.getHook().sendMessage(resultMessage).queue();

				return "";
			} else {
				return resultMessage;
			}
		} else {
			return lang.wrongUsage(commandStructure.getUsage());
		}
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
