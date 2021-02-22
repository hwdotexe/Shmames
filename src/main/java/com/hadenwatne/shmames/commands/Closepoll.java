package com.hadenwatne.shmames.commands;

import java.util.Date;
import java.util.Timer;
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
import com.hadenwatne.shmames.tasks.PollTask;

import javax.annotation.Nullable;

public class Closepoll implements ICommand {
	private Brain brain;
	private Lang lang;

	@Override
	public String getDescription() {
		return "End a Poll early (before its deadline), using the Poll's ID.";
	}
	
	@Override
	public String getUsage() {
		return "closepoll <poll ID>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Utils.checkUserPermission(brain.getSettingFor(BotSettingName.ALLOW_POLLS), message.getMember())) {
			Matcher m = Pattern.compile("^\\#?[a-zA-Z0-9]{5}$").matcher(args);

			if (m.find()) {
				// Omit any # at the beginning.
				if (args.startsWith("#")) {
					args = args.substring(1);
				}

				for (Poll p : brain.getActivePolls()) {
					if (p.getID().equalsIgnoreCase(args)) {
						// Start a PollTask that ends early
						Timer t = new Timer();
						t.schedule(new PollTask(p, message.getGuild().getTextChannelById(p.getChannelID()).retrieveMessageById(p.getMessageID()).complete()), new Date());

						return "";
					}
				}

				return lang.getError(Errors.NOT_FOUND, true);
			} else {
				return lang.wrongUsage(getUsage());
			}
		}else{
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"closepoll", "close poll", "endpoll", "end poll"};
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
}
