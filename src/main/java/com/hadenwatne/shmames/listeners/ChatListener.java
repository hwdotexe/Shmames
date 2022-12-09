package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commands.Dev;
import com.hadenwatne.shmames.commands.ForumWeapon;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.ForumWeaponObj;
import com.hadenwatne.shmames.models.Response;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.GachaUser;
import com.hadenwatne.shmames.services.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ChatListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {
			Message message = e.getMessage();

			// Messages from a Guild may contain additional data or context for the bot.
			if(e.isFromGuild()) {
				Guild server = e.getGuild();
				Brain brain = App.Shmames.getStorageService().getBrain(server.getId());

				// A command did not run, so perform other tasks.
				tallyServerEmotes(message, server, brain);

				// Send a random message. Randomly.
				if (RandomService.GetRandom(175) == 0) {
					sendRandom(message, TriggerType.RANDOM, brain);
				}

				// Gacha points!
				int randomPoints = RandomService.GetRandom(3);

				if (randomPoints > 0) {
					GachaUser gachaUser = GachaService.GetGachaUser(brain, e.getAuthor());

					gachaUser.addUserPoints(randomPoints);
				}
			} else {
				// Custom text-based command for bot admin.
				if (e.getChannelType() == ChannelType.PRIVATE) {
					if (message.getAuthor().getId().equals(App.Shmames.getStorageService().getMotherBrain().getBotAdminID())) {
						final String botNameLower = App.Shmames.getBotName().toLowerCase();
						final String messageText = message.getContentRaw();

						if (messageText.toLowerCase().startsWith(botNameLower)) {
							final String command = messageText.substring(botNameLower.length()).trim();

							if(command.startsWith("dev")) {
								final String devCmd = command.substring("dev".length()).trim();

								EmbedBuilder reply = Dev.run(message, devCmd);

								MessageService.ReplyToMessage(message, reply, false);
							}
						}
					} else {
						EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, ErrorKeys.HEY_THERE.name())
								.setDescription(App.Shmames.getLanguageService().getDefaultLang().getError(ErrorKeys.HEY_THERE));

						MessageService.ReplyToMessage(message, embed, false);
					}
				}
			}
		}
	}

	private void tallyServerEmotes(Message message, Guild server, Brain brain) {
		for (CustomEmoji emo : message.getMentions().getCustomEmojis()) {
			if (server.getEmojiById(emo.getId()) != null) {
				String id = Long.toString(emo.getIdLong());

				ShmamesService.IncrementEmoteTally(brain, id);
			}
		}
	}

	private void sendRandom(Message message, TriggerType t, Brain brain) {
		Member author = message.getMember();

		if (author != null) {
			String authorName = author.getNickname() != null ? author.getNickname() : author.getEffectiveName();
			List<Response> responses = brain.getResponsesFor(t);

			if (responses.size() > 0) {
				Response response = RandomService.GetRandomObjectFromList(responses);
				String responseValue = response.getResponse();

				responseValue = responseValue.replaceAll("%NAME%", authorName);

				EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, t.name() + " Response");

				switch(response.getResponseType()) {
					case GIF:
						try {
							String gifURL = HTTPService.GetGIF(responseValue, "high");
							InputStream file = new URL(gifURL).openStream();
							embedBuilder.setImage("attachment://result.gif");

							MessageService.ReplyToMessage(message, file, "result.gif", embedBuilder, false);
						} catch (Exception e) {
							LoggingService.LogException(e);
						}

						return;
					case FORUMWEAPON:
						ForumWeaponObj forumWeapon = ForumWeapon.FindForumWeapon(responseValue, brain, message.getGuild());

						if(forumWeapon != null) {
							MessageService.ReplySimpleMessage(message, forumWeapon.getItemLink(), false);
							break;
						}
					default:
						embedBuilder.setDescription(responseValue);

						MessageService.ReplyToMessage(message, embedBuilder, false);
				}
			}
		}
	}
}
