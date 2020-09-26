package com.hadenwatne.discordbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.discordbot.storage.Locale;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Poll;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.BotSettingName;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class Startpoll implements ICommand {
	private Locale locale;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Starts a new poll in the current channel, and pins it if configured. Example: " +
				"`startpoll 3h What is your favorite color? Red; Blue; Green`";
	}
	
	@Override
	public String getUsage() {
		return "startpoll <time>[d/h/m/s] <question>? <option>; <option>;...";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Utils.CheckUserPermission(brain.getSettingFor(BotSettingName.ALLOW_POLLS), message.getMember())) {
			Matcher m = Pattern.compile("^(\\d{1,3})([dhms])? (.+\\?) ((.+); (.+))$").matcher(args);

			if (m.find()) {
				int time = Integer.parseInt(m.group(1));
				String interval = m.group(2); // Could be empty!
				String question = m.group(3);
				String opt = m.group(4);

				// Bugfix: replace channel names within the question.
				Matcher bf = Pattern.compile("<#(\\d{5,})>").matcher(question);

				if (bf.find()) {
					question = bf.replaceFirst("#" + message.getGuild().getTextChannelById(bf.group(1)).getName());
				}
				// End bugfix

				List<String> options = new ArrayList<String>();

				for (String s : opt.split(";")) {
					options.add(s.trim());
				}

				if (options.size() > 1 && options.size() <= 9) {
					try {
						message.delete().queue();
					} catch (Exception e) {
						// Do nothing; we don't have permission
					}

					brain.getActivePolls().add(new Poll(message.getChannel(), question, options, time, interval, Utils.createID()));
				} else {
					return Errors.INCORRECT_ITEM_COUNT;
				}
			} else {
				// Regex fail
				return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
			}
		}else{
			return Errors.NO_PERMISSION_USER;
		}
		
		return "";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"startpoll", "start poll"};
	}

	@Override
	public void setRunContext(Locale locale, @Nullable Brain brain) {
		this.locale = locale;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
