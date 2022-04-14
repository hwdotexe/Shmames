package com.hadenwatne.shmames.commands;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.DataService;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

public class ListEmoteStats implements ICommand {
	private final CommandStructure commandStructure;

	public ListEmoteStats() {
		this.commandStructure = CommandBuilder.Create("listemotestats", "View emote usage statistics.")
				.addAlias("list emote stats")
				.addAlias("showemotestats")
				.addAlias("show emote stats")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		Guild server = data.getServer();
		StringBuilder statMsg = new StringBuilder("**" + lang.getMsg(Langs.EMOTE_STATS_TITLE) + "**\n");
		HashMap<String, Integer> emStats = new HashMap<String, Integer>(brain.getEmoteStats());

		// Add emotes without any uses
		for (Emote e : server.getEmotes()) {
			if (!emStats.containsKey(Long.toString(e.getIdLong()))) {
				emStats.put(Long.toString(e.getIdLong()), 0);
			}
		}

		// Sort
		LinkedHashMap<String, Integer> emotes = DataService.SortHashMap(emStats);

		// Send to the server
		if (emotes.keySet().size() > 0) {
			int i = 0;

			for (String em : emotes.keySet()) {
				Emote emote = server.getEmoteById(em);

				if (emote != null) {
					i++;

					if (i > 5) {
						statMsg.append("\n");
						i = 1;
					}

					statMsg.append(emote.getAsMention())
							.append(": ")
							.append(emotes.get(em))
							.append("  ");
				}
			}
		} else {
			statMsg.append("\nThere's nothing here!");
		}

		return statMsg.toString();
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}
}
