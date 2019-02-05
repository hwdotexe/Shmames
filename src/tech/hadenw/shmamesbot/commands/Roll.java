package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class Roll implements ICommand {
	@Override
	public String getDescription() {
		return "Just roll some dice.";
	}

	@Override
	public String run(String args, User author, Guild server) {
		if(Pattern.compile("^d\\d{1,3}([\\+\\-]\\d{1,2})?$").matcher(args).matches()) {
			try {
				String input = args.replaceAll("\\+", "p").replaceAll("\\-", "m");
				
				int base = 0;
				int mod = 0;
				boolean subtract = false;
				
				
				if(input.contains("p") || input.contains("m")) {
					// Has a modifier
					int mindex = input.contains("p") ? input.indexOf("p") : input.indexOf("m");
					
					base = Integer.parseInt(input.substring(1, mindex));
					mod = Integer.parseInt(input.substring(mindex+1));
					subtract = input.charAt(mindex) == 'm' ? true : false;
				} else {
					// No modifier
					base = Integer.parseInt(input.substring(1));
				}
				
				int roll = Shmames.getRandom(base) + 1;
				int baseroll = roll;
				
				if(subtract) {
					roll -= mod;
				}else {
					roll += mod;
				}
				
				return ":game_die: " + author.getAsMention() + " d"+base+"("+baseroll+")"+ (mod != 0 ? subtract == true ? "-"+mod : "+"+mod : "") +": " + (roll);
			}catch(Exception ex) {
				ex.printStackTrace();
				
				return "I sense a plot to destroy me.";
			}
		}else {
			return "Try `roll a d20`!";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"roll a"};
	}
}
