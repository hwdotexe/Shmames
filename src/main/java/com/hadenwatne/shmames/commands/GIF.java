package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Utils;

import javax.annotation.Nullable;

public class GIF implements ICommand {
	private Lang lang;

	@Override
	public String getDescription() {
		return "Send an awesome, randomly-selected GIF based on a search term.";
	}
	
	@Override
	public String getUsage() {
		return "gif <search>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0) {
			if(message.getChannelType() == ChannelType.TEXT){
				if(message.getTextChannel().isNSFW()){
					return Utils.getGIF(args, "low");
				}
			}

			return Utils.getGIF(args, "high");
		} else {
			return lang.getError(Errors.INCOMPLETE, true);
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"gif", "who is", "what is"};
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
