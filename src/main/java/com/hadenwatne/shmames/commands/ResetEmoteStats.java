package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.Brain;

import javax.annotation.Nullable;

public class ResetEmoteStats implements ICommand {
	private Lang lang;
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
		if(Utils.checkUserPermission(brain.getSettingFor(BotSettingName.RESET_EMOTE_STATS), message.getMember())){
			brain.getEmoteStats().clear();

			return lang.getMsg(Langs.RESET_EMOTE_STATS);
		}else{
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"resetemotestats", "reset emote stats"};
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
