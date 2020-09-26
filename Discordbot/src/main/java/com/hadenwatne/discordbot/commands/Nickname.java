package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Locale;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.BotSetting;
import com.hadenwatne.discordbot.storage.BotSettingName;
import com.hadenwatne.discordbot.storage.Brain;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nickname implements ICommand {
	private Brain brain;

	@Override
	public String getDescription() {
		return "Change the bot's nickname.";
	}
	
	@Override
	public String getUsage() {
		return "nickname <new nickname>";
	}

	@Override
	public String run(String args, User author, Message message) {
		BotSetting canChangeNickname = brain.getSettingFor(BotSettingName.ALLOW_NICKNAME);

		if(Utils.CheckUserPermission(canChangeNickname, message.getGuild().getMember(author))) {
			Matcher m = Pattern.compile("^[\\w\\s]{3,}$").matcher(args);

			if(m.find()){
				Member bot = message.getGuild().getMember(Shmames.getJDA().getSelfUser());

				try {
					bot.modifyNickname(args).queue();

					return "Nickname changed!";
				}catch (Exception e){
					return Errors.NO_PERMISSION_BOT;
				}
			}

			return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
		}else{
			return Errors.NO_PERMISSION_USER;
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"nickname", "nick"};
	}

	@Override
	public void setRunContext(Locale locale, @Nullable Brain brain) {
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
