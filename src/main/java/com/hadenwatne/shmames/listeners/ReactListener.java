package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.models.command.CommandMessagingChannel;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.commands.ICommand;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.data.Brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReactListener extends ListenerAdapter {
	private List<String> talliedMessageCache;

	public ReactListener(){
		talliedMessageCache = new ArrayList<>();
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getUser() != Shmames.getJDA().getSelfUser()) {
			if(e.isFromGuild()) {
				ReactionEmote emo = e.getReaction().getReactionEmote();

				// Only test custom server Emotes.
				if (emo.isEmote()) {
					Guild server = e.getGuild();

					if (server.getEmotes().contains(emo.getEmote())) {
						Brain b = Shmames.getBrains().getBrain(server.getId());
						Message message = e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete();
						String removalEmoteID = b.getSettingFor(BotSettingName.REMOVAL_EMOTE).getValue();
						String approvalEmoteID = b.getSettingFor(BotSettingName.APPROVAL_EMOTE).getValue();

						Utils.incrementEmoteTally(b, emo.getId());

						if (emo.getId().equals(removalEmoteID)) {
							tallyMessage(removalEmoteID, message, b, BotSettingName.REMOVAL_THRESHOLD);
							return;
						}

						if (emo.getId().equals(approvalEmoteID)) {
							tallyMessage(approvalEmoteID, message, b, BotSettingName.APPROVAL_THRESHOLD);
						}
					}
				}
			}
		}
	}

	private void tallyMessage(String emoteID, Message message, Brain brain, BotSettingName setting) {
		int threshold = Integer.parseInt(brain.getSettingFor(setting).getValue());
		int votes = 0;

		// Set the votes variable to this emote's count on the message.
		for(MessageReaction r : message.getReactions()) {
			if(r.getReactionEmote().isEmote()) {
				if (r.getReactionEmote().getId().equalsIgnoreCase(emoteID)) {
					votes = r.getCount();

					break;
				}
			}
		}

		// Give the user a tally based on the emote used.
		if(votes == threshold){
			if(!talliedMessageCache.contains(message.getId())) {
				talliedMessageCache.add(message.getId());

				User author = message.getAuthor();
				MessageChannel channel = message.getChannel();
				String tallyPrefix = setting == BotSettingName.APPROVAL_THRESHOLD ? "bad" : "good";
				String toTally;

				if(author.getId().equals(Shmames.getJDA().getSelfUser().getId())) {
					toTally = tallyPrefix+"bot";
				} else {
					String authorName = author.getName().replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
					toTally = tallyPrefix+authorName;
				}

				runAddTallyCommand(toTally, brain, channel);

				// Try to delete the message if this was a "bad" tally.
				if(setting == BotSettingName.REMOVAL_THRESHOLD) {
					try {
						channel.deleteMessageById(message.getIdLong()).queue();
					} catch (Exception ex) {
						channel.sendMessage(Shmames.getDefaultLang().getError(Errors.NO_PERMISSION_BOT, true)).queue();
					}
				}
			}
		}
	}

	private void runAddTallyCommand(String tallyValue, Brain brain, MessageChannel channel) {
		for (ICommand c : Shmames.getCommandHandler().getLoadedCommands()) {
			if (c.getCommandStructure().getName().equalsIgnoreCase("addtally")) {
				HashMap<String, Object> tallyArgs = new HashMap<>();

				tallyArgs.put("toTally", tallyValue);

				ShmamesCommandData data = new ShmamesCommandData(
						c,
						new ShmamesCommandArguments(tallyArgs),
						new CommandMessagingChannel(channel),
						Shmames.getJDA().getSelfUser()
				);

				String response = c.run(Shmames.getLangFor(brain), brain, data);
				channel.sendMessage(response).queue();
				return;
			}
		}
	}
}