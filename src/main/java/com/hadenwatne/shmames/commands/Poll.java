package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.ShmamesLogger;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.tasks.PollTask;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Poll implements ICommand {
	private final CommandStructure commandStructure;

	public Poll() {
		this.commandStructure = CommandBuilder.Create("poll", "Create and manage server polls.")
				.addSubCommands(
						CommandBuilder.Create("start", "Begin a new poll in the channel.")
								.addParameters(
										new CommandParameter("time", "The amount of time the poll should last.", ParameterType.TIMECODE),
										new CommandParameter("question", "The question to ask", ParameterType.STRING)
												.setPattern(".+\\?"),
										new CommandParameter("options", "The poll's options, separated by ';'", ParameterType.STRING)
												.setPattern("(.+;)+(.+);?")
								)
								.build(),
						CommandBuilder.Create("close", "Close an existing poll.")
								.addParameters(
										new CommandParameter("pollID", "The ID of the poll to close.", ParameterType.STRING)
												.setPattern("#?[a-z0-9]{5}")
								)
								.build()
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getExamples() {
		return "`poll start 12h30m Pizza or Burgers? Pizza; Burgers;`\n" +
				"`poll close #12345`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		if (Utils.checkUserPermission(data.getServer(), brain.getSettingFor(BotSettingName.ALLOW_POLLS), data.getAuthor())) {
			String subCmd = data.getSubCommand().getCommandName();
			ShmamesCommandArguments subCmdArgs = data.getSubCommand().getArguments();

			switch (subCmd.toLowerCase()) {
				case "start":
					return startPoll(subCmdArgs, lang, brain, data.getServer(), data.getMessagingChannel());
				case "close":
					return closePoll(subCmdArgs, brain, lang);
				default:
					return lang.wrongUsage(commandStructure.getUsage());
			}
		} else {
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}

	private String closePoll(ShmamesCommandArguments args, Brain brain, Lang lang) {
		String pollID = args.getAsString("pollID");

		if (pollID.startsWith("#")) {
			pollID = pollID.substring(1);
		}

		for (PollModel p : brain.getActivePolls()) {
			if (p.getID().equalsIgnoreCase(pollID)) {
				Timer t = new Timer();
				t.schedule(new PollTask(p), new Date());

				return "";
			}
		}

		return lang.getError(Errors.NOT_FOUND, true);
	}

	private String startPoll(ShmamesCommandArguments args, Lang lang, Brain brain, Guild server, ShmamesCommandMessagingChannel messagingChannel) {
		String time = args.getAsString("time");
		String question = args.getAsString("question");
		String options = args.getAsString("options");
		int seconds = Utils.convertTimeStringToSeconds(time);

		if (seconds > 0) {
			// Use friendly channel names when possible.
			Matcher channelReference = Pattern.compile("<#(\\d{15,})>").matcher(question);

			while (channelReference.find()) {
				TextChannel textChannel = server.getTextChannelById(channelReference.group(1));

				if (textChannel != null) {
					question = question.replaceFirst(channelReference.group(1), textChannel.getName());
				}
			}

			List<String> optionsList = new ArrayList<>();

			for (String s : options.split(";")) {
				optionsList.add(s.trim());
			}

			if (optionsList.size() > 1 && optionsList.size() <= 9) {
				try {
					if (messagingChannel.hasOriginMessage()) {
						messagingChannel.getOriginMessage().delete().queue();
					}
				} catch (InsufficientPermissionException e) {
					// Do nothing; we don't have permission
				} catch (Exception e) {
					ShmamesLogger.logException(e);
				}

				PollModel poll = new PollModel(messagingChannel.getChannel().getId(), question, optionsList, seconds, Utils.createID());
				brain.getActivePolls().add(poll);

				return "";
			} else {
				return lang.getError(Errors.INCORRECT_ITEM_COUNT, true);
			}
		} else {
			return lang.getError(Errors.TIME_VALUE_INCORRECT, false);
		}
	}
}
