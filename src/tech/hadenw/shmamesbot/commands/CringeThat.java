package tech.hadenw.shmamesbot.commands;

import java.util.ArrayList;
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
				
				String[] creepyAsterisks = new String[] {"*nuzzles*", "*soft*", "*nosebleed*", "*sobs*", "*meows*", "*smiles*", "*boops*", "*shy*", "*sniffs*", "*pounces*", "*cuddles*", "*hugs*", "*poke*", "*purr*"};
				String[] creepyOwos = new String[] {"Owo", ">w<", "UwU", "OwO", "x3", ">^<", ";3", "^~^"};
				
				// Some basic cringe
				cringe = cringe.replace("r", "w").replace("R", "W").replace("l", "w").replace("L", "W");
				cringe = cringe.replace(" th", " d").replaceAll("th ", "f ");
				
				// Stutter
				for(int i=0; i<Utils.getRandom(4)+1; i++) {
					int pos = getStringPosition(cringe);
					
					cringe = cringe.substring(0, pos) + cringe.charAt(pos) + "-" + cringe.substring(pos);
				}
				
				// Asterisks
				for(int i=0; i<Utils.getRandom(3); i++) {
					String rItem = creepyAsterisks[Utils.getRandom(creepyAsterisks.length)];
					int pos = getStringPosition(cringe);
					
					cringe = cringe.substring(0, pos) + "`"+rItem+"`" + " " + cringe.substring(pos);
				}
				
				// Owo
				for(int i=0; i<Utils.getRandom(4); i++) {
					String rItem = creepyOwos[Utils.getRandom(creepyOwos.length)];
					int pos = getStringPosition(cringe);
					
					cringe = cringe.substring(0, pos) + "`"+rItem+"`" + " " + cringe.substring(pos);
				}
				
				return cringe;
			}catch(Exception ex) {
				ex.printStackTrace();
				return Errors.NO_PERMISSION_BOT;
			}
		}
		
		return Errors.formatUsage(Errors.INCOMPLETE, getUsage());
	}
	
	// Returns the position of a word that is safe to interject text into
	private int getStringPosition(String src) {
		String[] words= src.split(" ");
		List<String> safeWords = new ArrayList<String>();
		
		Pattern p = Pattern.compile("^[a-zA-Z]");
		
		for(int i=0; i<words.length; i++) {
			if(p.matcher(words[i]).find()) {
				safeWords.add(words[i]);
			}
		}
		
		String rWord = safeWords.get(Utils.getRandom(safeWords.size()));
		
		return src.indexOf(rWord);
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
