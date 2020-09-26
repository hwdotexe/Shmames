package com.hadenwatne.discordbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Locale;
import com.hadenwatne.discordbot.storage.Locales;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Utils;

import javax.annotation.Nullable;

public class Choose implements ICommand {
	private Locale locale;

	@Override
	public String getDescription() {
		return "Let me make a decision for you.";
	}
	
	@Override
	public String getUsage() {
		return "choose <item> or <item>";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(.{1,}) or (.{1,})$", Pattern.CASE_INSENSITIVE).matcher(args);
		
		if(m.find()) {
			int mutator = Utils.getRandom(50);
			
			if(mutator < 5) { // 10%
				return locale.getMsg(Locales.CHOOSE, new String[] { "Neither" });
			} else if(mutator < 10) { // 20%
				return locale.getMsg(Locales.CHOOSE, new String[] { "Both" });
			} else {
				String c = m.group(1 + Utils.getRandom(2));

				return locale.getMsg(Locales.CHOOSE, new String[] { c });
			}
		}else {
			return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"choose"};
	}

	@Override
	public void setRunContext(Locale locale, @Nullable Brain brain) {
		this.locale = locale;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
