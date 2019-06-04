package tech.hadenw.shmamesbot.commands;

import java.util.List;
import java.util.regex.Pattern;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Utils;

public class CringeThat implements ICommand {
	@Override
	public String getDescription() {
		return "*is kawaii* UwU";
	}
	
	@Override
	public String getUsage() {
		return "cringeThat <^...>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(Pattern.compile("^[\\^]{1,15}$").matcher(args).matches()) {
			try {
				int messages = args.length();
				
				List<Message> msgs = message.getChannel().getHistoryBefore(message, messages).complete().getRetrievedHistory();
				Message toPin = msgs.get(msgs.size()-1);
				
				String cringe = toPin.getContentDisplay();
				
				String[] creepyAsterisks = new String[] {"`*nuzzles you*`", "`*soft*`", "`*nosebleed*`", "`*sobs*`", "`*meows*`", "`*smiles*`", "`*boops*`", "`*shy*`", "`*sniffs you*`", "`*pounces on you*`", "`*cuddles*`", "`*hugs*`", "`*pokes you*`"};
				String[] creepyOwos = new String[] {"Owo", ">w<", "UwU", "OwO", "x3", ">^<", ";3", "^~^"};
				
				// Some basic cringe
				cringe = cringe.substring(0, 1).toUpperCase() + "-" + cringe;
				cringe = cringe.replace("r", "w").replace("R", "W").replace("l", "w").replace("L", "W").replace("th", "f");
				
				if(Utils.getRandom(7) == 1) {
					String rItem = creepyAsterisks[Utils.getRandom(creepyAsterisks.length)];
					
					int pos = cringe.indexOf(" ", Utils.getRandom(cringe.length()));
					
					cringe = cringe.substring(0, pos) + " " + rItem + " " + cringe.substring(pos+1);
				}
				
				if(Utils.getRandom(5) == 1) {
					String rItem = creepyOwos[Utils.getRandom(creepyOwos.length)];
					
					int pos = cringe.indexOf(" ", Utils.getRandom(cringe.length()));
					
					cringe = cringe.substring(0, pos) + " " + rItem + " " + cringe.substring(pos+1);
				}
				
				return cringe;
			}catch(Exception ex) {
				ex.printStackTrace();
				return Errors.NO_PERMISSION_BOT;
			}
		}
		
		return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
	}

	@Override
	public String[] getAliases() {
		return new String[] {"cringethat", "cringe that"};
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
