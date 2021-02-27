package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.ShmamesLogger;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.tasks.PollTask;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Poll implements ICommand {
	private Brain brain;
	private Lang lang;

	@Override
	public String getDescription() {
		return "Create and manage server polls.\n\n" +
				"Example: `poll start 12h30m Pizza or Burgers? Pizza; Burgers;`\n" +
				"Example: `poll close #12345`";
	}
	
	@Override
	public String getUsage() {
		return "poll <start|close> [<time> <question> <options>|<pollID>]";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Utils.checkUserPermission(brain.getSettingFor(BotSettingName.ALLOW_POLLS), message.getMember())) {
			Matcher m = Pattern.compile("^((start)|(close))\\s(.+)$", Pattern.CASE_INSENSITIVE).matcher(args);

			if (m.find()) {
				String subCmd = m.group(1);
				String subCmdArgs = m.group(4);

				switch(subCmd.toLowerCase()) {
					case "start":
						return startPoll(subCmdArgs, message);
					case "close":
						return closePoll(subCmdArgs);
					default:
						return lang.wrongUsage(getUsage());
				}
			} else {
				return lang.wrongUsage(getUsage());
			}
		}else{
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"poll", "survey"};
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

	private String closePoll(String args) {
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

	private String startPoll(String args, Message message) {
		Matcher m = Pattern.compile("^([\\dydhms]+)\\s(.+\\?) ((.+); (.+))$", Pattern.CASE_INSENSITIVE).matcher(args);

		if (m.find()) {
			int seconds = Utils.convertTimeStringToSeconds(m.group(1));
			String question = m.group(2);
			String opt = m.group(3);

			if(seconds > 0) {
				Matcher channelReference = Pattern.compile("<#(\\d{15,})>").matcher(question);

				while (channelReference.find()) {
					TextChannel textChannel = message.getGuild().getTextChannelById(channelReference.group(1));

					if(textChannel != null) {
						question = question.replaceFirst(channelReference.group(1), textChannel.getName());
					}
				}

				List<String> options = new ArrayList<String>();

				for (String s : opt.split(";")) {
					options.add(s.trim());
				}

				if (options.size() > 1 && options.size() <= 9) {
					try {
						message.delete().queue();
					} catch (InsufficientPermissionException e) {
						// Do nothing; we don't have permission
					} catch (Exception e) {
						ShmamesLogger.logException(e);
					}

					PollModel poll = new PollModel(message.getTextChannel(), question, options, seconds, Utils.createID());
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
