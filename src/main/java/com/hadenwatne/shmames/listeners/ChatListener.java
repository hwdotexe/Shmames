package com.hadenwatne.shmames.listeners;

import com.hadenwatne.botcore.App;
import com.hadenwatne.botcore.service.LoggingService;
import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.shmames.Shmames;
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
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener extends ListenerAdapter {
	private Shmames shmames;

	public ChatListener(Shmames shmames) {
		this.shmames = shmames;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {
			Message message = e.getMessage();

			// Messages from a Guild may contain additional data or context for the bot.
			if (e.isFromGuild()) {
				Guild server = e.getGuild();
				Brain brain = shmames.getStorageService().getBrain(server.getId());

				if (RandomService.GetRandom(175) == 0) {
					sendRandom(e.getMessage(), TriggerType.RANDOM, brain);
				}

				// Gacha points!
				int randomPoints = RandomService.GetRandom(5) + 1;
				GachaUser gachaUser = GachaService.GetGachaUser(brain, e.getAuthor());

				gachaUser.addUserPoints(randomPoints);
			}
		}
	}

	private void sendRandom(Message message, TriggerType t, Brain brain) {
		Member author = message.getMember();

		if (author != null) {
			String authorName = author.getNickname() != null ? author.getNickname() : author.getEffectiveName();
			List<Response> responses = brain.getResponsesFor(t);

			if (!responses.isEmpty()) {
				Response response = RandomService.GetRandomObjectFromList(responses);
				String responseValue = response.getResponse();

				responseValue = responseValue.replaceAll("%NAME%", authorName);

				EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, t.name() + " Response");

				switch (response.getResponseType()) {
					case GIF:
						try {
							String gifURL = HTTPService.GetGIF(responseValue, "high");
							InputStream file = new URL(gifURL).openStream();
							embedBuilder.setImage("attachment://result.gif");

							MessageService.ReplyToMessage(message, file, "result.gif", embedBuilder, false);
						} catch (Exception e) {
							App.getLogger().LogException(e);
						}

						return;
					case FORUMWEAPON:
						ForumWeaponObj forumWeapon = ForumWeapon.FindForumWeapon(responseValue, brain, message.getGuild());

						if (forumWeapon != null) {
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