package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;

public class Poll extends Command {
	public Poll() {
		super(true);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("poll", "Create server polls.")
				.addParameters(
						new CommandParameter("time", "The amount of time the poll should last.", ParameterType.TIMECODE)
								.setExample("24h"),
						new CommandParameter("question", "The question to ask", ParameterType.STRING)
								.setPattern(".+\\?")
								.setExample("Thoughts?"),
						new CommandParameter("options", "The poll's options, separated by ';'", ParameterType.STRING)
								.setPattern("(.+;)+(.+);?")
								.setExample("Yes; No; Maybe")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		Guild server = executingCommand.getServer();
		Brain brain = executingCommand.getBrain();
		Lang lang = executingCommand.getLanguage();

		if (ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.POLL_CREATE), executingCommand.getAuthorUser())) {
			String time = executingCommand.getCommandArguments().getAsString("time");
			String question = executingCommand.getCommandArguments().getAsString("question");
			String options = executingCommand.getCommandArguments().getAsString("options");
			int seconds = DataService.ConvertTimeStringToSeconds(time);

			if (seconds > 0) {
				List<String> optionsList = new ArrayList<>();

				for (String s : options.split(";")) {
					optionsList.add(s.trim());
				}

				if (optionsList.size() > 1 && optionsList.size() <= 26) {
					EmbedBuilder embedBuilder = response(EmbedType.SUCCESS)
							.setDescription(lang.getMsg(Langs.GENERIC_SUCCESS));

					executingCommand.reply(embedBuilder, false, onSuccess -> {
						PollModel poll = new PollModel(executingCommand.getChannel().getId(), executingCommand.getAuthorUser().getId(), onSuccess.getId(), question, optionsList, seconds);
						brain.getActivePolls().add(poll);

						poll.startPollInstrumentation();
					});

					return null;
				} else {
					return response(EmbedType.ERROR, Errors.INCORRECT_ITEM_COUNT.name())
							.setDescription(lang.getError(Errors.INCORRECT_ITEM_COUNT));
				}
			} else {
				return response(EmbedType.ERROR, Errors.TIME_VALUE_INCORRECT.name())
						.setDescription(lang.getError(Errors.TIME_VALUE_INCORRECT));
			}
		} else {
			return response(EmbedType.ERROR, Errors.NO_PERMISSION_USER.name())
					.setDescription(lang.getError(Errors.NO_PERMISSION_USER));
		}
	}
}
