package com.hadenwatne.discordbot.commands;

import java.util.HashMap;
import java.util.LinkedHashMap;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.discordbot.Shmames;
import com.hadenwatne.discordbot.Utils;
import com.hadenwatne.discordbot.storage.Brain;

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
			if(!emStats.containsKey(Long.toString(e.getIdLong()))) {
				emStats.put(Long.toString(e.getIdLong()), 0);
			}
		}
		
		// Sort
		LinkedHashMap<String, Integer> emotes = Utils.sortHashMap(emStats);
		
		// Send to the server
		if(emotes.keySet().size() > 0) {
			int i = 0;
			
			for(String em : emotes.keySet()) {
				Emote emote = message.getGuild().getEmoteById(em);
				
				if(emote != null) {
					i++;
					
					if(i > 5) {
						statMsg += "\n";
						i = 1;
					}
					
					statMsg += emote.getAsMention() + ": " + emotes.get(em)+"  ";
				}
			}
		}else {
			statMsg += "\nThere's nothing here!";
		}

		return statMsg;
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
