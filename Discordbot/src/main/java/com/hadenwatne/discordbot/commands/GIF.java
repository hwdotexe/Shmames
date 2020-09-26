package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Lang;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Utils;

import javax.annotation.Nullable;

public class GIF implements ICommand {
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
			return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"gif", "who is", "what is", "what are"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {

	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
