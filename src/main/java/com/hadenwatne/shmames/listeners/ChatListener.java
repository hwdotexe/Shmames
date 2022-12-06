package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commands.Command;
import com.hadenwatne.shmames.commands.ForumWeapon;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.ForumWeaponObj;
import com.hadenwatne.shmames.models.Response;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.GachaUser;
import com.hadenwatne.shmames.models.data.Language;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {
			Message message = e.getMessage();
			final String messageText = message.getContentRaw();

			// Messages from a Guild may contain additional data or context for the bot.
			if(e.isFromGuild()) {
				Guild server = e.getGuild();
				Brain brain = App.Shmames.getStorageService().getBrain(server.getId());

				// Check if this message is trying to run a command.
				for (String trigger : brain.getTriggers().keySet()) {
					if (brain.getTriggers().get(trigger) == TriggerType.COMMAND) {
						if (messageText.toLowerCase().startsWith(trigger.toLowerCase())) {
							Language language = App.Shmames.getLanguageService().getLangFor(brain);

							if (messageText.trim().length() == trigger.length()) {
								EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.INFO)
										.setDescription(language.getError(ErrorKeys.HEY_THERE, new String[]{App.Shmames.getBotName()}));

								MessageService.ReplyToMessage(message, embed, false);

								return;
							}

							final String command = messageText.substring(trigger.length()).trim();

							handleCommand(message, command, brain, language);

							return;
						}
					}
				}

				// A command did not run, so perform other tasks.
				tallyServerEmotes(message, server, brain);

				// Handle other trigger types.
				boolean didTrigger = handleResponseTriggers(brain, message);

				// Send a random message. Randomly.
				if(!didTrigger) {
					if (RandomService.GetRandom(175) == 0) {
						sendRandom(e.getMessage(), TriggerType.RANDOM, brain);
					}

					// Gacha points!
					int messageLength = messageText.split("\\s").length;
					int randomPoints = 0;

					if(messageLength > 50) {
						randomPoints = RandomService.GetRandom(15) + 1;
					}else if(messageLength > 30) {
						randomPoints = RandomService.GetRandom(10) + 1;
					} else if(messageLength > 10) {
						randomPoints = RandomService.GetRandom(5) + 1;
					}

					if(randomPoints > 0) {
						GachaUser gachaUser = GachaService.GetGachaUser(brain, e.getAuthor());

						gachaUser.addUserPoints(randomPoints);
					}
				}
			} else {
				// Messages sent to the bot directly are limited to basic commands.
				if (e.getChannelType() == ChannelType.PRIVATE || e.getChannelType() == ChannelType.GROUP) {
					final String botNameLower = App.Shmames.getBotName().toLowerCase();

					if (messageText.toLowerCase().startsWith(botNameLower)) {
						final String command = messageText.substring(botNameLower.length()).trim();

						handleCommand(message, command, null, App.Shmames.getLanguageService().getDefaultLang());
					}
				}
			}
		}
	}

	private void handleCommand(Message message, String commandText, Brain brain, Language language) {
		Command command = App.Shmames.getCommandHandler().PreProcessCommand(commandText);
		ExecutingCommand executingCommand = new ExecutingCommand(language, brain);

		if(command != null) {
			if(!command.isSlashOnly()){
				executingCommand.setCommandName(command.getCommandStructure().getName());
				executingCommand.setMessage(message);

				App.Shmames.getCommandHandler().HandleCommand(command, executingCommand, commandText);
			}else{
				EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, ErrorKeys.SLASH_ONLY.name())
						.setDescription(language.getError(ErrorKeys.SLASH_ONLY));

				MessageService.ReplyToMessage(message, embed, false);
			}
		} else {
			EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, ErrorKeys.COMMAND_NOT_FOUND.name())
					.setDescription(language.getError(ErrorKeys.COMMAND_NOT_FOUND));

			MessageService.ReplyToMessage(message, embed, false);
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

	private boolean handleResponseTriggers(Brain brain, Message message) {
		for (String trigger : brain.getTriggers().keySet()) {
			TriggerType type = brain.getTriggers().get(trigger);

			if(type != TriggerType.COMMAND) {
				Matcher m = Pattern.compile("(^"+trigger+")|([\\s\\\b]"+trigger+"\\b)", Pattern.CASE_INSENSITIVE).matcher(message.getContentRaw());

				if(m.find()){
					sendRandom(message, type, brain);

					return true;
				}
			}
		}

		return false;
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
