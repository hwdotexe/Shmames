package tech.hadenw.discordbot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Utils;

public class CringeThat implements ICommand {
	@Override
	public String getDescription() {
		return "Rewrite a previous message in a cringy way. Use `^` symbols to specify the " +
				"message to rewrite.";
	}
	
	@Override
	public String getUsage() {
		return "cringeThat <^...> [times]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^([\\^]{1,15})( \\d{1,2})?$").matcher(args);
		
		if(m.find()) {
			try {
				int messages = m.group(1).length();
				int iterations = 1;
				
				if(m.group(2) != null) {
					iterations = Integer.parseInt(m.group(2).trim());
				}
				
				List<Message> msgs = message.getChannel().getHistoryBefore(message, messages).complete().getRetrievedHistory();
				Message toPin = msgs.get(msgs.size()-1);
				
				String cringe = toPin.getContentDisplay();
				
				String[] creepyAsterisks = new String[] {"*nuzzles*", "*soft*", "*nosebleed*", "*sobs*", "*meows*", "*smiles*", "*boops*", "*shy*", "*sniffs*", "*pounces*", "*cuddles*", "*hugs*", "*poke*", "*purr*",
						"*curious*", "*moves closer*", "*licks*", "*stares*", "*gag*", "*bites lip*"};
				String[] creepyOwos = new String[] {"Owo", ">w<", "UwU", "OwO", "x3", ">^<", ";3", "^~^"};
				HashMap<String, String> cringeDict = new HashMap<String, String>();
				cringeDict.put("food", "numsies"); cringeDict.put("can't", "nu can"); cringeDict.put("have", "hab");
				cringeDict.put("and", "an"); cringeDict.put("stupid", "stoopi"); cringeDict.put("dumb", "no smart");
				
				for(int it=0; it<iterations; it++) {
					// Replace words
					for(String word : cringeDict.keySet()) {
						cringe = cringe.replace(word, cringeDict.get(word));
					}
					
					// Add more owos
					if(Utils.getRandom(4)==1) {
						Pattern p = Pattern.compile("(.+o)[\\.\\?\\!\\~]?$");
						String[] cw = cringe.split(" ");
						String[] ncw = new String[cw.length];
						
						for(int i=0; i<cw.length; i++) {
							Matcher mat = p.matcher(cw[i]);
							
							if(mat.find()) {
								ncw[i] = cw[i]+"wo";
							}else {
								ncw[i] = cw[i];
							}
						}
						
						cringe = "";
						
						for(String w : ncw) {
							if(cringe.length() > 0)
								cringe += " ";
							
							cringe += w;
						}
					}
					
					// Some basic cringe
					cringe = cringe.replace("r", "w").replace("R", "W").replace("l", "w").replace("L", "W");
					cringe = cringe.replace(" th", " d").replaceAll("th ", "f ");
					
					// Stutter
					for(int i=0; i<Utils.getRandom(4)+1; i++) {
						int pos = getStringPosition(cringe);
						
						if(pos > 0)
							cringe = cringe.substring(0, pos) + cringe.charAt(pos) + "-" + cringe.substring(pos);
					}
					
					// Asterisks
					for(int i=0; i<Utils.getRandom(3); i++) {
						String rItem = creepyAsterisks[Utils.getRandom(creepyAsterisks.length)];
						int pos = getStringPosition(cringe);
						
						if(pos > 0)
							cringe = cringe.substring(0, pos) + "`"+rItem+"`" + " " + cringe.substring(pos);
					}
					
					// Owo
					for(int i=0; i<Utils.getRandom(3); i++) {
						String rItem = creepyOwos[Utils.getRandom(creepyOwos.length)];
						int pos = getStringPosition(cringe);
						
						if(pos > 0)
							cringe = cringe.substring(0, pos) + "`"+rItem+"`" + " " + cringe.substring(pos);
					}
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
		
		Pattern p = Pattern.compile("^[a-zA-Z]{3,}");
		
		for(int i=0; i<words.length; i++) {
			if(p.matcher(words[i].trim()).find()) {
				safeWords.add(words[i]);
			}
		}
		
		if(safeWords.size() > 0) {
			String rWord = safeWords.get(Utils.getRandom(safeWords.size()));
			
			return src.indexOf(rWord);
		}else {
			return -1;
		}
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
