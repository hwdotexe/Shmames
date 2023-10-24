package com.hadenwatne.shmames.listeners;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;
import com.hadenwatne.shmames.models.RoleMessage;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.services.ShmamesService;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Shmames includes a quick way for server members to self-moderate or self-promote. If this is on, then
 * adding an "approval" or "removal" emote will cause the bot to adjust tallies accordingly.
 */
public class ReactListener extends ListenerAdapter {
	private Shmames shmames;

	public ReactListener(Shmames shmames) {
		this.shmames = shmames;
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		e.retrieveUser().queue(user -> {
			// Ignore bot reactions and reactions in private channels.
			if (!user.isBot()) {
				if (e.isFromGuild()) {
					EmojiUnion emoteUnion = e.getReaction().getEmoji();

					// Make sure that the reacted emote is a custom emoji from this server (ignore Nitro reactions).
					try {
						CustomEmoji emote = emoteUnion.asCustom();
						Guild server = e.getGuild();

						if (server.getEmojiById(emote.getId()) != null) {
							// Begin processing.
							Brain brain = shmames.getStorageService().getBrain(server.getId());

							// Handle role reactions first.
							for (RoleMessage roleMessage : brain.getRoleMessages()) {
								if (e.getMessageId().equals(roleMessage.getMessageID())) {
									Role role = server.getRoleById(roleMessage.getEmoteRoleMap().get(emote.getId()));

									if (role != null) {
										server.addRoleToMember(server.getMemberById(user.getIdLong()), role).queue();
									}

									return;
								}
							}

							// Regardless of emote used, tally up the usage for this server.
							ShmamesService.IncrementEmoteTally(brain, emote.getId());

							// Handle tally reactions
							if (brain.getSettingFor(BotSettingName.TALLY_REACTIONS).getAsBoolean()) {
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
					} catch (IllegalStateException ignored) {
						// Don't do anything if this is not a custom emote.
					}
				}
			}
		});
	}

	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		e.retrieveUser().queue(user -> {
			if (!user.isBot()) {
				if (e.isFromGuild()) {
					EmojiUnion emoteUnion = e.getReaction().getEmoji();

					try {
						CustomEmoji emote = emoteUnion.asCustom();
						Guild server = e.getGuild();

						if (server.getEmojiById(emote.getId()) != null) {
							Brain brain = shmames.getStorageService().getBrain(server.getId());

							// Handle role reactions.
							for (RoleMessage roleMessage : brain.getRoleMessages()) {
								if (e.getMessageId().equals(roleMessage.getMessageID())) {
									Role role = server.getRoleById(roleMessage.getEmoteRoleMap().get(emote.getId()));

									if (role != null) {
										server.removeRoleFromMember(server.getMemberById(user.getIdLong()), role).queue();
									}

									return;
								}
							}
						}
					} catch (IllegalStateException ignored) {
						// Do nothing
					}
				}
			}
		});
	}

	private void tallyMessage(String emoteID, Message message, Brain brain, BotSettingName setting) {
		int threshold = brain.getSettingFor(setting).getAsNumber();
		int votes = 0;

		// Set the votes variable to this emote's count on the message.
		for (MessageReaction r : message.getReactions()) {
			if (message.getGuild().getEmojiById(emoteID) != null) {
				if (r.getEmoji().asCustom().getId().equalsIgnoreCase(emoteID)) {
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

			if (author.getId().equals(shmames.getJDA().getSelfUser().getId())) {
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

		Command command = shmames.getCommandHandler().PreProcessCommand(commandText);
		Language language = shmames.getLanguageService().getLangFor(brain);
		ExecutingCommand executingCommand = new ExecutingCommand(language, brain);

		if (command != null) {
			executingCommand.setCommandName(command.getCommandStructure().getName());
			executingCommand.setMessage(message);

			shmames.getCommandHandler().HandleCommand(command, executingCommand, commandText);
		}
	}
}