package com.hadenwatne.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Errors;
import com.hadenwatne.discordbot.Utils;

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
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
