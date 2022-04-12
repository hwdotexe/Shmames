package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.services.ShmamesService;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.models.Response;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.HTTPService;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener extends ListenerAdapter {
	private final Lang defaultLang = Shmames.getDefaultLang();
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {
			Message message = e.getMessage();
			String messageText = message.getContentRaw();

			if(e.isFromGuild()) {
				Brain brain = Shmames.getBrains().getBrain(e.getGuild().getId());
				Guild server = e.getGuild();

				// React to every message, even commands, with a PingPong emoji if Jinping Mode is on.
				if (brain.getJinping())
					e.getMessage().addReaction("\uD83C\uDFD3").queue();

				// Check if this message is trying to run a command.
				for (String trigger : brain.getTriggers(TriggerType.COMMAND)) {
					if (messageText.toLowerCase().startsWith(trigger.toLowerCase())) {
						String command = messageText.substring(trigger.length()).trim();

						// Send to the command handler for further processing.
						Shmames.getCommandHandler().PerformCommand(command, e.getMessage(), server, brain);

						return;
					}
				}

				// A command did not run, so perform other tasks.
				tallyServerEmotes(message, server, brain);

				// Handle other trigger types.
				boolean didTrigger = handleResponseTriggers(brain, message, server);

				// Send a random message. Randomly.
				if(!didTrigger) {
					if (RandomService.GetRandom(175) == 0) {
						sendRandom(e.getTextChannel(), TriggerType.RANDOM, e.getMessage());
					}
				}
			} else {
				if (e.getChannelType() == ChannelType.PRIVATE || e.getChannelType() == ChannelType.GROUP) {
					final String botNameLower = Shmames.getBotName().toLowerCase();

					if (messageText.toLowerCase().startsWith(botNameLower)) {
						String command = messageText.substring(botNameLower.length()).trim();

						// Send to the command handler for further processing.
						Shmames.getCommandHandler().PerformCommand(command, message,null, null);
					}
				}
			}
		}
	}

	private void tallyServerEmotes(Message message, Guild server, Brain brain) {
		for (Emote emo : message.getEmotes()) {
			if (server.getEmotes().contains(emo)) {
				String id = Long.toString(emo.getIdLong());

				ShmamesService.IncrementEmoteTally(brain, id);
			}
		}
	}

	private boolean handleResponseTriggers(Brain brain, Message message, Guild server) {
		for (TriggerType type : TriggerType.values()) {
			if (type != TriggerType.COMMAND) {
				for (String trigger : brain.getTriggers(type)) {
					Matcher m = Pattern.compile("\\b" + trigger + "\\b", Pattern.CASE_INSENSITIVE).matcher(message.getContentRaw());

					if (m.find()) {
						TextChannel channel = message.getTextChannel();

						if (type != TriggerType.REACT) {
							sendRandom(channel, type, message);
						} else {
							List<Emote> em = new ArrayList<>(server.getEmotes());

							for(Guild fg : ShmamesService.GetConnectedFamilyGuilds(brain, server)) {
								em.addAll(fg.getEmotes());
							}

							message.addReaction(em.get(RandomService.GetRandom(em.size()))).queue();
						}

						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Chooses a random response from the server's list for a given response trigger.
	 * @param c The channel to reply to.
	 * @param t The trigger type being called.
	 * @param message The message that triggered this event.
	 */
	private void sendRandom(TextChannel c, TriggerType t, Message message) {
		Guild g = message.getGuild();
		Member author = message.getMember();
		List<Response> r = Shmames.getBrains().getBrain(g.getId()).getResponsesFor(t);
		String name = author.getNickname() != null ? author.getNickname() : author.getEffectiveName();

		if(r.size() > 0) {
			String response = r.get(RandomService.GetRandom(r.size())).getResponse().replaceAll("%NAME%", name);

			if (response.startsWith("[gif]"))
				response = HTTPService.GetGIF(response.split("\\[gif\\]", 2)[1], c.isNSFW() ? "low" : "high");

			if(t == TriggerType.LOVE || t == TriggerType.HATE) {
				message.reply(response).queue();
			} else {
				c.sendMessage(response).queue();
			}
		}else{
			if(t != TriggerType.RANDOM)
				c.sendMessage("There are no responses saved for the "+t.name()+" type!").queue();
		}
	}
}
