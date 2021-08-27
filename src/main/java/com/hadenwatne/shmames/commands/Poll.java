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
		this.commandStructure = CommandBuilder.Create("poll")
				.addParameters(
						new CommandParameter("action", "The action to perform", ParameterType.SELECTION)
								.addSelectionOptions("start", "close"),
						new CommandParameter("data", "Command options", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Create and manage server polls.";
	}

	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`poll start 12h30m Pizza or Burgers? Pizza; Burgers;`\n" +
				"`poll close #12345`";
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		if (Utils.checkUserPermission(data.getServer(), brain.getSettingFor(BotSettingName.ALLOW_POLLS), data.getAuthor())) {
			String subCmd = data.getArguments().getAsString("action");
			String subCmdArgs = data.getArguments().getAsString("data");

			switch (subCmd.toLowerCase()) {
				case "start":
					return startPoll(subCmdArgs, lang, brain, data.getServer(), data.getMessagingChannel());
				case "close":
					return closePoll(subCmdArgs, brain, lang);
				default:
					return lang.wrongUsage(getUsage());
			}
		} else {
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}

	private String closePoll(String args, Brain brain, Lang lang) {
		Matcher m = Pattern.compile("^\\#?[a-zA-Z0-9]{5}$").matcher(args);

		if (m.find()) {
			if (args.startsWith("#")) {
				args = args.substring(1);
			}

			for (PollModel p : brain.getActivePolls()) {
				if (p.getID().equalsIgnoreCase(args)) {
					Timer t = new Timer();
					t.schedule(new PollTask(p), new Date());

					return "";
				}
			}

			return lang.getError(Errors.NOT_FOUND, true);
		} else {
			return lang.wrongUsage(getUsage());
		}
	}

	private String startPoll(String args, Lang lang, Brain brain, Guild server, ShmamesCommandMessagingChannel messagingChannel) {
		Matcher m = Pattern.compile("^([\\dydhms]+)\\s(.+\\?) ((.+); (.+))$", Pattern.CASE_INSENSITIVE).matcher(args);

		if (m.find()) {
			int seconds = Utils.convertTimeStringToSeconds(m.group(1));
			String question = m.group(2);
			String opt = m.group(3);

			if (seconds > 0) {
				// Use friendly channel names when possible.
				Matcher channelReference = Pattern.compile("<#(\\d{15,})>").matcher(question);

				while (channelReference.find()) {
					TextChannel textChannel = server.getTextChannelById(channelReference.group(1));

					if (textChannel != null) {
						question = question.replaceFirst(channelReference.group(1), textChannel.getName());
					}
				}

				List<String> options = new ArrayList<String>();

				for (String s : opt.split(";")) {
					options.add(s.trim());
				}

				if (options.size() > 1 && options.size() <= 9) {
					try {
						if (messagingChannel.hasOriginMessage()) {
							messagingChannel.getOriginMessage().delete().queue();
						}
					} catch (InsufficientPermissionException e) {
						// Do nothing; we don't have permission
					} catch (Exception e) {
						ShmamesLogger.logException(e);
					}

					PollModel poll = new PollModel(messagingChannel.getChannel().getId(), question, options, seconds, Utils.createID());
					brain.getActivePolls().add(poll);

					return "";
				} else {
					return lang.getError(Errors.INCORRECT_ITEM_COUNT, true);
				}
			}
		}

		return lang.wrongUsage(getUsage());
	}
}
