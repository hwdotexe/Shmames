package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Utils;

public class Roll implements ICommand {
	@Override
	public String getDescription() {
		return "Just roll some dice.";
	}
	
	@Override
	public String getUsage() {
		return "roll a d<#>[+#]";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^d\\d{1,3}([\\+\\-]\\d{1,2})?$").matcher(args).matches()) {
			try {
				String input = args.replaceAll("\\+", "p").replaceAll("\\-", "m");
				
				int base = 0;
				int mod = 0;
				boolean subtract = false;
				
				if(input.contains("p") || input.contains("m")) {
					// Has a modifier
					int mindex = input.contains("p") ? input.indexOf("p") : input.indexOf("m");
					
					base = Integer.parseInt(input.substring(input.indexOf("d")+1, mindex));
					mod = Integer.parseInt(input.substring(mindex+1));
					subtract = input.charAt(mindex) == 'm' ? true : false;
				} else {
					// No modifier
					base = Integer.parseInt(input.substring(input.indexOf("d")+1));
				}
				
				String a = "";
				int roll = Utils.getRandom(base) + 1;
				int baseroll = roll;
				
				if(subtract) {
					roll -= mod;
				}else {
					roll += mod;
				}
				
				if(a.length() > 0)
					a += "\n";
				
				a += ":game_die: " + author.getAsMention() + " d"+base+"("+baseroll+")"+ (mod != 0 ? subtract == true ? "-"+mod : "+"+mod : "") +": " + (roll);
				
				
				// Send memes
				if(base == 20) {
					if(baseroll <= 3) {
						a = a + "\n" + Utils.getGIF("laugh");
					} else if(baseroll >= 19) {
						a = a + "\n" + Utils.getGIF("hype");
					}
				}
				
				// Delete the person's message
				try {
					message.delete().complete();
				}catch(Exception e) {
					// Do nothing, because the bot probably doesn't have permission.
				}
				
				return a;
			}catch(Exception ex) {
				ex.printStackTrace();
				return Errors.BOT_ERROR;
			}
		}else {
			return "Try `roll a d20`!";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"roll a"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.replaceAll("[\\W&&[^\\+\\-]]", "").toLowerCase();
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
