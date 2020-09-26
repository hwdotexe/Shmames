package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Locale;
import com.hadenwatne.discordbot.storage.Locales;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.BotSettingName;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;

public class ResetEmoteStats implements ICommand {
	private Locale locale;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Reset emote usage statistics.";
	}
	
	@Override
	public String getUsage() {
		return "resetEmoteStats";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Utils.CheckUserPermission(brain.getSettingFor(BotSettingName.RESET_EMOTE_STATS), message.getMember())){
			brain.getEmoteStats().clear();

			return locale.getMsg(Locales.RESET_EMOTE_STATS);
		}else{
			return Errors.NO_PERMISSION_USER;
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"resetemotestats", "reset emote stats"};
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
