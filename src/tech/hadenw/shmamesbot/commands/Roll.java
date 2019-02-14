package tech.hadenw.shmamesbot.commands;

import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class Roll implements ICommand {
	@Override
	public String getDescription() {
		return "Just roll some dice.";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^\\d?\\d?d\\d{1,3}([\\+\\-]\\d{1,2})?$").matcher(args).matches()) {
			try {
				String input = args.replaceAll("\\+", "p").replaceAll("\\-", "m");
				
				int base = 0;
				int mod = 0;
				boolean subtract = false;
				
				int iterations = 1;
				if(!input.startsWith("d")) {
					iterations = Integer.parseInt(input.substring(0, input.indexOf("d")));
				}
				
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
				int baseroll = -1;
				
				for(int i=0; i<iterations; i++) {
					int roll = Shmames.getRandom(base) + 1;
					baseroll = roll;
					
					if(subtract) {
						roll -= mod;
					}else {
						roll += mod;
					}
					
					if(a.length() > 0)
						a += "\n";
					
					a += ":game_die: " + author.getAsMention() + " d"+base+"("+baseroll+")"+ (mod != 0 ? subtract == true ? "-"+mod : "+"+mod : "") +": " + (roll);
				}
				
				// Send memes
				if(base == 20 && iterations == 1) {
					if(baseroll <= 3) {
						a = a + "\n" + Shmames.getGIF("laugh");
					} else if(baseroll >= 19) {
						a = a + "\n" + Shmames.getGIF("hype");
					}
				}
				
				// Delete the person's message
				message.delete().complete();
				
				return a;
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
	
	@Override
	public String sanitize(String i) {
		return i.replaceAll("[\\W&&[^\\+\\-]]", "").toLowerCase();
	}
}
