package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.commands.Command;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.TriggerType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.Response;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
					if(brain.getTriggers().get(trigger) == TriggerType.COMMAND) {
						final String command = messageText.substring(trigger.length()).trim();

						handleCommand(message, command, brain);

						return;
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
				}
			} else {
				// Messages sent to the bot directly are limited to basic commands.
				if (e.getChannelType() == ChannelType.PRIVATE || e.getChannelType() == ChannelType.GROUP) {
					final String botNameLower = App.Shmames.getBotName().toLowerCase();

					if (messageText.toLowerCase().startsWith(botNameLower)) {
						final String command = messageText.substring(botNameLower.length()).trim();

						handleCommand(message, command, null);
					}
				}
			}
		}
	}

	private void handleCommand(Message message, String commandText, Brain brain) {
		Command command = App.Shmames.getCommandHandler().PreProcessCommand(commandText);
		Lang lang = App.Shmames.getLanguageService().getLangFor(brain);
		ExecutingCommand executingCommand = new ExecutingCommand(lang, brain);

		if(command != null) {
			executingCommand.setCommandName(command.getCommandStructure().getName());
			executingCommand.setMessage(message);

			App.Shmames.getCommandHandler().HandleCommand(command, executingCommand, commandText);
		} else {
			EmbedBuilder embed = EmbedFactory.GetEmbed(EmbedType.ERROR, Errors.COMMAND_NOT_FOUND.name())
					.addField(null, lang.getError(Errors.COMMAND_NOT_FOUND, false), false);

			MessageService.ReplyToMessage(message, embed);
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

	private boolean handleResponseTriggers(Brain brain, Message message) {
		for (String trigger : brain.getTriggers().keySet()) {
			TriggerType type = brain.getTriggers().get(trigger);

			if(type != TriggerType.COMMAND) {
				Matcher m = Pattern.compile("\\b" + trigger + "\\b", Pattern.CASE_INSENSITIVE).matcher(message.getContentRaw());

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

		if(author != null) {
			String authorName = author.getNickname() != null ? author.getNickname() : author.getEffectiveName();
			List<Response> responses = brain.getResponsesFor(t);

			if (responses.size() > 0) {
				String response =  RandomService.GetRandomObjectFromList(responses).getResponse();

				// Handle special responses.
				response = response.replaceAll("%NAME%", authorName);

				if (response.startsWith("[gif]")) {
					response = HTTPService.GetGIF(response.split("\\[gif\\]", 2)[1], "high");
				}

				// Send the response.
				MessageService.ReplyToMessage(message, EmbedType.INFO, t.name() + " Response", response);
			}
		}
	}
}
