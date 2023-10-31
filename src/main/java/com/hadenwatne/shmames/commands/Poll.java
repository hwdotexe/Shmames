package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.language.ErrorKey;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.language.Language;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;

public class Poll extends Command {
	public Poll() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ADD_REACTION};
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
		Language language = executingCommand.getLanguage();

		if (ShmamesService.CheckUserPermission(server, brain.getSettingFor(BotSettingName.POLL_CREATE), executingCommand.getAuthorMember())) {
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
							.setDescription(language.getMsg(LanguageKey.GENERIC_SUCCESS));

					executingCommand.reply(embedBuilder, false, onSuccess -> {
						PollModel poll = new PollModel(executingCommand.getChannel().getId(), executingCommand.getAuthorUser().getId(), onSuccess.getId(), question, optionsList, seconds);
						brain.getActivePolls().add(poll);

						poll.startPollInstrumentation();
					});

					return null;
				} else {
					return response(EmbedType.ERROR, ErrorKey.INCORRECT_ITEM_COUNT.name())
							.setDescription(language.getError(ErrorKey.INCORRECT_ITEM_COUNT));
				}
			} else {
				return response(EmbedType.ERROR, ErrorKey.TIME_VALUE_INCORRECT.name())
						.setDescription(language.getError(ErrorKey.TIME_VALUE_INCORRECT));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKey.NO_PERMISSION_USER.name())
					.setDescription(language.getError(ErrorKey.NO_PERMISSION_USER));
		}
	}
}
