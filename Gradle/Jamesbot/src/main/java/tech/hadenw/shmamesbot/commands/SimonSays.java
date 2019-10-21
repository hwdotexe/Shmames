package tech.hadenw.shmamesbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Shmames;

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
			
			while(m.find()) {
				String eName = m.group(1);
				List<Emote> e = new ArrayList<Emote>();
				
				for(Guild g : Shmames.getJDA().getGuilds()) {
					List<Emote> em = g.getEmotesByName(eName, true);
					
					if(em.size() > 0) {
						e.add(em.get(0));
						break;
					}
				}
				
				if(e.size() > 0) {
					args = args.replace(":"+eName+": ", e.get(0).getAsMention());
				}
			}
			
			return args;
		}else {
			return Errors.formatUsage(Errors.WRONG_USAGE, getUsage());
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"simonsays","simon says"};
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
