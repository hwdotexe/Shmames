package com.hadenwatne.shmames.commands;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.models.Brain;

import javax.annotation.Nullable;

public class ListEmoteStats implements ICommand {
	private Brain brain;
	private Lang lang;

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
		StringBuilder statMsg = new StringBuilder("**" + lang.getMsg(Langs.EMOTE_STATS_TITLE) + "**\n");
		HashMap<String, Integer> emStats = new HashMap<String, Integer>(brain.getEmoteStats());
		
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
						statMsg.append("\n");
						i = 1;
					}
					
					statMsg.append(emote.getAsMention())
							.append(": ")
							.append(emotes.get(em))
							.append("  ");
				}
			}
		}else {
			statMsg.append("\nThere's nothing here!");
		}

		return statMsg.toString();
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listemotestats", "list emote stats"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
