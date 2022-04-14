package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import com.hadenwatne.shmames.models.command.ShmamesCommandArguments;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.services.ShmamesService;
import com.hadenwatne.shmames.commands.ICommand;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.data.Brain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ReactListener extends ListenerAdapter {
	private List<String> talliedMessageCache;

	public ReactListener(){
		talliedMessageCache = new ArrayList<>();
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getUser() != App.Shmames.getJDA().getSelfUser()) {
			if(e.isFromGuild()) {
				ReactionEmote emo = e.getReaction().getReactionEmote();

				// Only test custom server Emotes.
				if (emo.isEmote()) {
					Guild server = e.getGuild();

					if (server.getEmotes().contains(emo.getEmote())) {
						Brain b = App.Shmames.getStorageService().getBrain(server.getId());
						Message message = e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete();
						String removalEmoteID = b.getSettingFor(BotSettingName.REMOVAL_EMOTE).getValue();
						String approvalEmoteID = b.getSettingFor(BotSettingName.APPROVAL_EMOTE).getValue();

						ShmamesService.IncrementEmoteTally(b, emo.getId());

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
				String tallyPrefix = setting == BotSettingName.APPROVAL_THRESHOLD ? "good" : "bad";
				String toTally;

				if(author.getId().equals(App.Shmames.getJDA().getSelfUser().getId())) {
					toTally = tallyPrefix+"bot";
				} else {
					String authorName = author.getName().replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
					toTally = tallyPrefix+authorName;
				}

				runAddTallyCommand(toTally, brain, message);

				// Try to delete the message if this was a "bad" tally.
				if(setting == BotSettingName.REMOVAL_THRESHOLD) {
					try {
						channel.deleteMessageById(message.getIdLong()).queue();
					} catch (Exception ex) {
						channel.sendMessage(App.Shmames.getLanguageService().getDefaultLang().getError(Errors.NO_PERMISSION_BOT, true)).queue();
					}
				}
			}
		}
	}

	private void runAddTallyCommand(String tallyValue, Brain brain, Message message) {
		MessageChannel channel = message.getTextChannel();

		for (ICommand c : App.Shmames.getCommandHandler().getLoadedCommands()) {
			if (c.getCommandStructure().getName().equalsIgnoreCase("addtally")) {
				LinkedHashMap<String, Object> tallyArgs = new LinkedHashMap<>();

				tallyArgs.put("tallyName", tallyValue);

				ShmamesCommandData data = new ShmamesCommandData(
						c,
						new ShmamesCommandArguments(tallyArgs),
						new ShmamesCommandMessagingChannel(message, channel),
						App.Shmames.getJDA().getSelfUser(),
						message.getGuild()
				);

				String response = c.run(App.Shmames.getLanguageService().getLangFor(brain), brain, data);
				channel.sendMessage(response).queue();
				return;
			}
		}
	}
}