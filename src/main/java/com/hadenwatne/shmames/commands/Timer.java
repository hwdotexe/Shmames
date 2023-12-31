package com.hadenwatne.shmames.commands;

import com.hadenwatne.corvus.Corvus;
import com.hadenwatne.corvus.CorvusBuilder;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.language.ErrorKey;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.AlarmModel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.tasks.AlarmTask;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Timer extends Command {
	private final Shmames shmames;

	public Timer(Shmames shmames) {
		super(true);
		this.shmames = shmames;
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected Permission[] configureRequiredUserPermissions() {
		return null;
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("timer", "Start a timer and be alerted when it's ready.")
				.addParameters(
						new CommandParameter("duration", "The amount of time before the timer runs", ParameterType.TIMECODE)
								.setExample("1d12h"),
						new CommandParameter("message", "The description of the timer", ParameterType.STRING)
								.setExample("Pizza time!")
				)
				.build();
	}

	@Override
	public void onCommandFailure(Execution execution) {

	}

	@Override
	public void run(Execution execution) {
		String duration = execution.getArguments().get("duration").getAsString();
		String msg = execution.getArguments().get("message").getAsString();
		int seconds = DataService.ConvertTimeStringToSeconds(duration);
		User author = execution.getUser();

		if (seconds > 0 && seconds <= 31536000) {
			String timeMsg = getTimeMsg(seconds);
			CorvusBuilder builder = Corvus.success(execution.getBot());

			builder.setDescription(execution.getLanguageProvider().getMessageFromKey(execution, LanguageKey.TIMER_STARTED.name(), timeMsg));

			builder.setSuccessCallback(message -> {
				message.retrieveOriginal().queue(original -> {
					Brain brain = shmames.getBrainController().getBrain(execution.getServer().getId());
					AlarmModel model = new AlarmModel(execution.getChannel().getId(), original.getId(), author.getId(), seconds, msg);

					brain.getTimers().add(model);

					// Begin this timer's countdown.
					java.util.Timer t = new java.util.Timer();
					t.schedule(new AlarmTask(model, brain, shmames), model.getExecTime());
				});
			});

			Corvus.reply(execution, builder);
		} else {
			CorvusBuilder builder = Corvus.error(execution.getBot());

			builder.setDescription(execution.getLanguageProvider().getErrorFromKey(execution, ErrorKey.TIME_VALUE_INCORRECT.name()));
			builder.setEphemeral();

			Corvus.reply(execution, builder);
		}
	}

	@NotNull
	private static String getTimeMsg(int seconds) {
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
		String timeMsg = (f_day > 0 ? f_day + "d " : "") + (f_hour > 0 ? f_hour + "h " : "") + (f_min > 0 ? f_min + "m " : "") + (f_sec > 0 ? f_sec + "s" : "");
		return timeMsg;
	}
}