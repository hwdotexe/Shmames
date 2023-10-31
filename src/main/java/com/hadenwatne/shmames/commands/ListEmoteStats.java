package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.shmames.services.PaginationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ListEmoteStats extends Command {
	public ListEmoteStats() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("listemotestats", "View emote usage statistics.")
				.addAlias("showemotestats")
				.addAlias("show emote stats")
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		Guild server = executingCommand.getServer();
		final String headerMessage = executingCommand.getLanguage().getMsg(LanguageKey.EMOTE_STATS_TITLE);
		StringBuilder statMsg = new StringBuilder();
		HashMap<String, Integer> emStats = new HashMap<String, Integer>(executingCommand.getBrain().getEmoteStats());

		// Add emotes without any uses
		for (CustomEmoji e : server.getEmojis()) {
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
				CustomEmoji emote = server.getEmojiById(em);

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

		List<String> splitEmoteList = PaginationService.SplitString(statMsg.toString(), MessageEmbed.VALUE_MAX_LENGTH);
		EmbedBuilder embed = response(EmbedType.INFO)
				.setDescription(headerMessage);

		for (String s : splitEmoteList) {
			embed.addField("", s, false);
		}

		return embed;
	}
}
