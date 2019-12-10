package tech.hadenw.shmamesbot.commands;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.Brain;

public class SimonSays implements ICommand {
	@Override
	public String getDescription() {
		return "I say what you say, eh?";
	}
	
	@Override
	public String getUsage() {
		return "simonsays <message>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0) {
			try {
				message.delete().complete();
			} catch(Exception e) { }
			
			
			// Try to scan all servers for the emote?
			// :emotename:   =>   <a:emoteName:1234567890>
			Matcher m = Pattern.compile("\\:([\\w\\d]+)\\:").matcher(args);
			
			// Add a space at the end so we can regex correctly
			args += " ";
			
			
			// Check servers for the emote; this one first, then others.
			while(m.find()) {
				String eName = m.group(1);
				
				// This guild
				Emote e = findEmote(message.getGuild(), eName);
				
				if(e != null) {
					args = args.replace(":"+eName+": ", e.getAsMention());
				} else {
					// Check all guilds
					for(Guild g : Shmames.getJDA().getGuilds()) {
						e = findEmote(g, eName);
						
						if(e != null) {
							args = args.replace(":"+eName+": ", e.getAsMention());
							break;
						}
					}
				}
				
				// Tally the emote
				if(e != null) {
					Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());
					String name = e.getName();
	
					if(b.getEmoteStats().containsKey(name)) {
						b.getEmoteStats().put(name, b.getEmoteStats().get(name)+1);
					}else {
						b.getEmoteStats().put(name, 1);
					}
				}
			}
			
			return args;
		}else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"simonsays","simon says", "repeat"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
	
	private Emote findEmote(Guild g, String n) {
		List<Emote> em = g.getEmotesByName(n, true);
		
		if(em.size() > 0)
			return em.get(0);
		
		return null;
	}
}
