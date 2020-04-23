package tech.hadenw.discordbot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.Utils;
import tech.hadenw.discordbot.storage.BotSetting;
import tech.hadenw.discordbot.storage.BotSettingName;
import tech.hadenw.discordbot.storage.Brain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nickname implements ICommand {
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
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		BotSetting canChangeNickname = b.getSettingFor(BotSettingName.ALLOW_NICKNAME);

		if(Utils.CheckUserPermission(canChangeNickname, message.getGuild().getMember(author))) {
			Matcher m = Pattern.compile("^[\\w\\s]{3,}$").matcher(args);

			if(m.find()){
				Member bot = message.getGuild().getMember(Shmames.getJDA().getSelfUser());

				try {
					bot.modifyNickname(m.group(1)).queue();

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
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
