package com.hadenwatne.discordbot.commands;

import com.hadenwatne.discordbot.storage.Brain;
import com.hadenwatne.discordbot.storage.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Utils;

import javax.annotation.Nullable;

public class NewSeed implements ICommand {
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public String getUsage() {
		return "newseed (seed)";
	}
	
	// Laura's Dice Jail
	@Override
	public String run(String args, User author, Message message) {
		long seed = System.currentTimeMillis();
		
		if(args.length()>0) {
			try {
				seed = Long.parseLong(args);
				Utils.GetRandomObj().setSeed(seed);
			}catch(Exception e) {
				return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
			}
		}else {
			Utils.GetRandomObj().setSeed(seed);
		}
		
		return ":game_die: New seed: "+seed;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"newseed", "new seed"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {

	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
