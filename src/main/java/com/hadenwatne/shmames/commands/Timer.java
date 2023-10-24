package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.types.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.tasks.AlarmTask;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.TimeUnit;

public class Timer extends Command {
	public Timer() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("timer", "Start a timer and be alerted when it's ready.")
				.addAlias("remind me in")
				.addAlias("alert")
				.addAlias("alarm")
				.addParameters(
						new CommandParameter("duration", "The amount of time before the timer runs", ParameterType.TIMECODE)
								.setExample("1d12h"),
						new CommandParameter("message", "The description of the timer", ParameterType.STRING, false)
								.setExample("Pizza time!")
				)
				.build();
	}

	@Override
	public EmbedBuilder run(ExecutingCommand executingCommand) {
		String duration = executingCommand.getCommandArguments().getAsString("duration");
		String msg = executingCommand.getCommandArguments().getAsString("message");
		int seconds = DataService.ConvertTimeStringToSeconds(duration);
		User author = executingCommand.getAuthorUser();

		if (seconds > 0 && seconds <= 31536000) {
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
			String messageID = executingCommand.hasMessage() ? executingCommand.getMessage().getId() : null;
			AlarmTask t = new AlarmTask(executingCommand.getChannel().getIdLong(), messageID, seconds, msg, executingCommand.getLanguage().getMsg(LanguageKeys.TIMER_ALERT, new String[]{author.getAsMention()}));
			String timeMsg = (f_day > 0 ? f_day + "d " : "") + (f_hour > 0 ? f_hour + "h " : "") + (f_min > 0 ? f_min + "m " : "") + (f_sec > 0 ? f_sec + "s" : "");

			executingCommand.getBrain().getTimers().add(t);

			return response(EmbedType.SUCCESS)
					.setDescription(executingCommand.getLanguage().getMsg(LanguageKeys.TIMER_STARTED, new String[]{"**" + timeMsg + "**"}));
		} else {
			return response(EmbedType.ERROR, ErrorKeys.TIMER_LENGTH_INCORRECT.name())
					.setDescription(executingCommand.getLanguage().getError(ErrorKeys.TIMER_LENGTH_INCORRECT));
		}
	}
}