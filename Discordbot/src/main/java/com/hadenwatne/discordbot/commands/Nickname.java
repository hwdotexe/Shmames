package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.*;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.storage.Errors;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nickname implements ICommand {
	private Brain brain;
	private Lang lang;

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

					return lang.getMsg(Langs.GENERIC_SUCCESS);
				}catch (Exception e){
					return lang.getError(Errors.NO_PERMISSION_BOT, true);
				}
			}

			return lang.getError(Errors.INCOMPLETE, true);
		}else{
			return lang.getError(Errors.NO_PERMISSION_USER, true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"nickname", "nick"};
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
