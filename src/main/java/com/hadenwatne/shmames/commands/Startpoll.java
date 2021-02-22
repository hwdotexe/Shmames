package com.hadenwatne.shmames.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.models.Poll;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.Brain;

import javax.annotation.Nullable;

public class Startpoll implements ICommand {
	private Lang lang;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Starts a new poll in the current channel, and pins it if configured.\n" +
				"Example: `startpoll 1d12h Which color looks best? Red; Blue; Green;`";
	}
	
	@Override
	public String getUsage() {
		return "startpoll <time> <question>? <option>; <option>;...";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Utils.checkUserPermission(brain.getSettingFor(BotSettingName.ALLOW_POLLS), message.getMember())) {
			Matcher m = Pattern.compile("^(\\d{1,3}[dhms])+ (.+\\?) ((.+); (.+))$").matcher(args);

			if (m.find()) {
				int seconds = Utils.convertTimeStringToSeconds(m.group(1));
				String question = m.group(2);
				String opt = m.group(3);

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

					brain.getActivePolls().add(new Poll(message.getChannel(), question, options, seconds, Utils.createID(), lang));
				} else {
					return lang.getError(Errors.INCORRECT_ITEM_COUNT, true);
				}
			} else {
				return lang.wrongUsage(getUsage());
			}
		}else{
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
		
		return "";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"startpoll", "start poll"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
