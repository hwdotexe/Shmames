package tech.hadenw.shmamesbot.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.brain.Brain;

public class ListEmoteStats implements ICommand {
	@Override
	public String getDescription() {
		return "View emote usage statistics.";
	}
	
	@Override
	public String getUsage() {
		return "listEmoteStats";
	}

	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		String statMsg = "**Thus sayeth the Shmames:**\n";
		HashMap<String, Integer> emStats = new HashMap<String, Integer>(b.getEmoteStats());
		
		// Add emotes without any uses
		for(Emote e : message.getGuild().getEmotes()) {
			if(!emStats.containsKey(e.getName())) {
				emStats.put(e.getName(), 0);
			}
		}
		
		// Sort
		LinkedHashMap<String, Integer> emotes = sortHashMapByValues(emStats);
		
		// Send to the server
		if(emotes.keySet().size() > 0) {
			int i = 0;
			
			for(String em : emotes.keySet()) {
				List<Emote> ems = message.getGuild().getEmotesByName(em, false);
				
				if(!ems.isEmpty()) {
					i++;
					
					if(i > 5) {
						statMsg += "\n";
						i = 1;
					}
					
					statMsg += ems.get(0).getAsMention() + ": " + emotes.get(em)+"  ";
				}
			}
		}else {
			statMsg += "\nThere's nothing here!";
		}

		return statMsg;
	}
	
	// From https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
	private LinkedHashMap<String, Integer> sortHashMapByValues(HashMap<String, Integer> passedMap) {
	    List<String> mapKeys = new ArrayList<>(passedMap.keySet());
	    List<Integer> mapValues = new ArrayList<>(passedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);

	    LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

	    Iterator<Integer> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        int val = valueIt.next();
	        Iterator<String> keyIt = mapKeys.iterator();

	        while (keyIt.hasNext()) {
	            String key = keyIt.next();
	            int comp1 = passedMap.get(key);
	            int comp2 = val;

	            if (comp1 == comp2) {
	                keyIt.remove();
	                sortedMap.put(key, val);
	                break;
	            }
	        }
	    }
	    return sortedMap;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listemotestats", "list emote stats"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
