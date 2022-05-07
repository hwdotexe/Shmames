package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commands.Command;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Shmames includes a quick way for server members to self-moderate or self-promote. If this is on, then
 * adding an "approval" or "removal" emote will cause the bot to adjust tallies accordingly.
 */
public class ReactListener extends ListenerAdapter {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		// Ignore bot reactions and reactions in private channels.
		if (!e.getUser().isBot()) {
			if (e.isFromGuild()) {
				ReactionEmote emote = e.getReaction().getReactionEmote();

				// Make sure that the reacted emote is a custom emoji from this server (ignore Nitro reactions).
				if (emote.isEmote()) {
					Guild server = e.getGuild();

					if (server.getEmotes().contains(emote.getEmote())) {
						// Begin processing.
						Brain brain = App.Shmames.getStorageService().getBrain(server.getId());

						// Regardless of emote used, tally up the usage for this server.
						ShmamesService.IncrementEmoteTally(brain, emote.getId());

						if(brain.getSettingFor(BotSettingName.TALLY_REACTIONS).getAsBoolean()) {
							String removalEmoteID = brain.getSettingFor(BotSettingName.REMOVAL_EMOTE).getAsString();
							String approvalEmoteID = brain.getSettingFor(BotSettingName.APPROVAL_EMOTE).getAsString();

							// If the emote was an "approval" or "removal" emote, react accordingly.
							if (!brain.getTalliedMessages().contains(e.getMessageId())) {
								if (emote.getId().equals(removalEmoteID)) {
									e.retrieveMessage().queue(success -> {
										tallyMessage(removalEmoteID, success, brain, BotSettingName.REMOVAL_THRESHOLD);
									});
								} else if (emote.getId().equals(approvalEmoteID)) {
									e.retrieveMessage().queue(success -> {
										tallyMessage(approvalEmoteID, success, brain, BotSettingName.APPROVAL_THRESHOLD);
									});
								}
							}
						}
					}
				}
			}
		}
	}

	private void tallyMessage(String emoteID, Message message, Brain brain, BotSettingName setting) {
		int threshold = brain.getSettingFor(setting).getAsNumber();
		int votes = 0;

		// Set the votes variable to this emote's count on the message.
		for (MessageReaction r : message.getReactions()) {
			if (r.getReactionEmote().isEmote()) {
				if (r.getReactionEmote().getId().equalsIgnoreCase(emoteID)) {
					votes = r.getCount();

					break;
				}
			}
		}

		// Give the user a tally based on the emote used.
		if (votes == threshold) {
			brain.getTalliedMessages().add(message.getId());

			User author = message.getAuthor();
			String tallyPrefix = setting == BotSettingName.APPROVAL_THRESHOLD ? "good" : "bad";
			String toTally;

			if (author.getId().equals(App.Shmames.getJDA().getSelfUser().getId())) {
				toTally = tallyPrefix + "bot";
			} else {
				String authorName = author.getName().replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
				toTally = tallyPrefix + authorName;
			}

			runAddTallyCommand(toTally, brain, message);

			// Try to delete the message if this was a "bad" tally.
			if(setting == BotSettingName.REMOVAL_THRESHOLD) {
				try {
					message.delete().queue();
				} catch (Exception ignored) {
					// Bot does not have message delete privileges, so do nothing.
				}
			}
		}
	}

	private void runAddTallyCommand(String tallyValue, Brain brain, Message message) {
		final String commandText = "tally add " + tallyValue;

		Command command = App.Shmames.getCommandHandler().PreProcessCommand(commandText);
		Lang lang = App.Shmames.getLanguageService().getLangFor(brain);
		ExecutingCommand executingCommand = new ExecutingCommand(lang, brain);

		if (command != null) {
			executingCommand.setCommandName(command.getCommandStructure().getName());
			executingCommand.setMessage(message);

			App.Shmames.getCommandHandler().HandleCommand(command, executingCommand, commandText);
		}
	}
}