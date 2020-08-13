package com.hadenwatne.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.BotSettingName;
import com.hadenwatne.discordbot.storage.Brain;

public class ResetEmoteStats implements ICommand {
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
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());

		if(Utils.CheckUserPermission(b.getSettingFor(BotSettingName.RESET_EMOTE_STATS), message.getMember())){
			b.getEmoteStats().clear();

			return "We didn't need those anyway ;} #StatsCleared!";
		}else{
			return Errors.NO_PERMISSION_USER;
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"resetemotestats", "reset emote stats"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
