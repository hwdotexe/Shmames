package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.models.CommandMessagingChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.hadenwatne.shmames.CommandHandler;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.commands.ICommand;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.Brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReactListener extends ListenerAdapter {
	private List<String> goodTallyCache;
	private List<String> badTallyCache;

	public ReactListener(){
		goodTallyCache = new ArrayList<String>();
		badTallyCache = new ArrayList<String>();
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if (e.getUser() != Shmames.getJDA().getSelfUser()) {
			ReactionEmote emo = e.getReaction().getReactionEmote();
			Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());

			// Tally up the emote.
			if(emo.isEmote()) {
				if(e.getGuild().getEmotes().contains(emo.getEmote())) {
					Utils.incrementEmoteTally(b, emo.getId());
				}

				// Removal emote.
				String removalEmote = Shmames.getBrains().getBrain(e.getGuild().getId()).getSettingFor(BotSettingName.REMOVAL_EMOTE).getValue();

				if (emo.getId().equals(removalEmote)) {
					badTallyMessage(removalEmote, e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete(), b);
					return;
				}

				// Approval emote.
				String approvalEmote = Shmames.getBrains().getBrain(e.getGuild().getId()).getSettingFor(BotSettingName.APPROVAL_EMOTE).getValue();

				if (emo.getId().equals(approvalEmote)) {
					goodTallyMessage(approvalEmote, e.getChannel().retrieveMessageById(e.getMessageIdLong()).complete(), b);
				}
			}
		}
	}

	/**
	 * When a message receives the "badtally" emote, check to see if it hit the threshold. If so,
	 * delete the message and increment the tally.
	 * @param removalEmote The name of the emote used to remove messages.
	 * @param m The Message this reaction occurred on.
	 */
	private void badTallyMessage(String removalEmote, Message m, Brain b) {
		int threshold = Integer.parseInt(Shmames.getBrains().getBrain(m.getGuild().getId()).getSettingFor(BotSettingName.REMOVAL_THRESHOLD).getValue());
		int votes = 0;

		for(MessageReaction r : m.getReactions()) {
			if(r.getReactionEmote().isEmote()) {
				if (r.getReactionEmote().getId().equalsIgnoreCase(removalEmote)) {
					votes = r.getCount();

					break;
				}
			}
		}

		if(votes == threshold){
			if(!badTallyCache.contains(m.getId())) {
				badTallyCache.add(m.getId());

				String name = m.getAuthor().getName();
				name = name.replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
				String toTally = name.equalsIgnoreCase(Shmames.getJDA().getSelfUser().getName()) ? "badbot" : "bad" + name;

				// Remove the message & process
				try {
					m.getChannel().deleteMessageById(m.getIdLong()).queue();

					for (ICommand c : Shmames.getCommandHandler().getLoadedCommands()) {
						if (c.getCommandStructure().getName().equalsIgnoreCase("addtally")) {
							HashMap<String, Object> tallyArgs = new HashMap<>();

							tallyArgs.put("toTally", toTally);

							String response = c.run(Shmames.getLangFor(b), b, tallyArgs, Shmames.getJDA().getSelfUser(), new CommandMessagingChannel(m.getChannel()));
							m.getChannel().sendMessage(response).queue();
							return;
						}
					}
				} catch (Exception ex) {
					m.getChannel().sendMessage(Shmames.getDefaultLang().getError(Errors.NO_PERMISSION_BOT, true)).queue();
				}
			}
		}
	}

	/**
	 * When a message receives the "goodtally" emote, check to see if it hit the threshold. If so,
	 * give the user a good tally.
	 * @param approvalEmote The name of the emote used to remove messages.
	 * @param m The Message this reaction occurred on.
	 */
	private void goodTallyMessage(String approvalEmote, Message m ,Brain b) {
		int threshold = Integer.parseInt(Shmames.getBrains().getBrain(m.getGuild().getId()).getSettingFor(BotSettingName.APPROVAL_THRESHOLD).getValue());
		int votes = 0;

		for(MessageReaction r : m.getReactions()) {
			if(r.getReactionEmote().isEmote()) {
				if (r.getReactionEmote().getId().equalsIgnoreCase(approvalEmote)) {
					votes = r.getCount();

					break;
				}
			}
		}

		if(votes == threshold){
			if(!goodTallyCache.contains(m.getId())) {
				goodTallyCache.add(m.getId());

				String name = m.getAuthor().getName();
				name = name.replaceAll("\\s", "_").replaceAll("[\\W]", "").toLowerCase();
				String toTally = name.equalsIgnoreCase(Shmames.getJDA().getSelfUser().getName()) ? "goodbot" : "good" + name;

				// Process.
				for (ICommand c : Shmames.getCommandHandler().getLoadedCommands()) {
					if (c.getCommandStructure().getName().equalsIgnoreCase("addtally")) {
						HashMap<String, Object> tallyArgs = new HashMap<>();

						tallyArgs.put("toTally", toTally);

						String response = c.run(Shmames.getLangFor(b), b, tallyArgs, Shmames.getJDA().getSelfUser(), new CommandMessagingChannel(m.getChannel()));
						m.getChannel().sendMessage(response).queue();
						return;
					}
				}
			}
		}
	}
}