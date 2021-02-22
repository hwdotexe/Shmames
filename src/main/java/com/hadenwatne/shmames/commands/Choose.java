package com.hadenwatne.shmames.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Utils;

import javax.annotation.Nullable;

public class Choose implements ICommand {
	private Lang lang;

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

		if (m.find()) {
			int mutator = Utils.getRandom(50);

			if (mutator < 5) { // 10%
				return lang.getMsg(Langs.CHOOSE, new String[]{"Neither"});
			} else if (mutator < 10) { // 20%
				return lang.getMsg(Langs.CHOOSE, new String[]{"Both"});
			} else {
				String c = m.group(1 + Utils.getRandom(2));

				return lang.getMsg(Langs.CHOOSE, new String[]{c});
			}
		} else {
			return lang.getError(Errors.INCOMPLETE, true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"choose"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
