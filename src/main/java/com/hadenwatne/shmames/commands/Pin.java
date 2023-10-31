package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandParameter;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.language.ErrorKey;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.fornax.service.LoggingService;
import com.hadenwatne.shmames.services.MessageService;
import com.hadenwatne.shmames.services.ShmamesService;
import com.hadenwatne.shmames.services.TextFormatService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class Pin extends Command {
	public Pin() {
		super(true);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("pin", "Sends a copy of the specified message over to the Pin Channel, if configured.")
				.addAlias("pinthat")
				.addParameters(
						new CommandParameter("position", "A number of carats (^) pointing to the message", ParameterType.STRING)
								.setPattern("([\\^]{1,15})")
								.setExample("^^^")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		int messages = executingCommand.getCommandArguments().getAsString("position").length();

		if(ShmamesService.CheckUserPermission(executingCommand.getServer(), executingCommand.getBrain().getSettingFor(BotSettingName.ALLOW_PIN), executingCommand.getAuthorMember())) {
			try {
				Message toPin = MessageService.GetMessageIndicated(executingCommand, messages);
				TextChannel channelToSendPin = executingCommand.getServer().getTextChannelById(executingCommand.getBrain().getSettingFor(BotSettingName.PIN_CHANNEL).getAsString());

				if (channelToSendPin != null) {

					EmbedBuilder response = response(EmbedType.INFO);
					InputStream file = new URL(toPin.getMember().getEffectiveAvatarUrl()).openStream();
					Calendar c = Calendar.getInstance();

					c.setTime(new Date());
					String date = TextFormatService.GetISO8601Date(c);

					response.setAuthor(toPin.getAuthor().getName(), null, "attachment://profile.png");
					response.setThumbnail("attachment://profile.png");
					response.setFooter("#" + toPin.getChannel().getName() + " - " + date + " - Pinned by @" + executingCommand.getAuthorUser().getAsTag(), null);

					StringBuilder msg = new StringBuilder();

					msg.append("\"");
					msg.append(toPin.getContentRaw());
					msg.append("\"");

					for (Attachment a : toPin.getAttachments()) {
						msg.append("\n");
						msg.append(a.getUrl());
					}
					response.setDescription(msg.toString());

					MessageService.SendMessage(channelToSendPin, file, "profile.png", response);

					return response(EmbedType.SUCCESS)
							.setDescription(executingCommand.getLanguage().getMsg(LanguageKey.GENERIC_SUCCESS));
				} else {
					return response(EmbedType.ERROR, ErrorKey.CHANNEL_NOT_FOUND.name())
							.setDescription(executingCommand.getLanguage().getError(ErrorKey.CHANNEL_NOT_FOUND));
				}
			} catch (NumberFormatException e) {
				LoggingService.LogException(e);

				return response(EmbedType.ERROR, ErrorKey.CHANNEL_NOT_FOUND.name())
						.setDescription(executingCommand.getLanguage().getError(ErrorKey.CHANNEL_NOT_FOUND));
			} catch (InsufficientPermissionException e) {
				LoggingService.LogException(e);

				return response(EmbedType.ERROR, ErrorKey.NO_PERMISSION_BOT.name())
						.setDescription(executingCommand.getLanguage().getError(ErrorKey.NO_PERMISSION_BOT));
			} catch (Exception e) {
				LoggingService.LogException(e);

				return response(EmbedType.ERROR, ErrorKey.BOT_ERROR.name())
						.setDescription(executingCommand.getLanguage().getError(ErrorKey.BOT_ERROR));
			}
		} else {
			return response(EmbedType.ERROR, ErrorKey.NO_PERMISSION_USER.name())
					.setDescription(executingCommand.getLanguage().getError(ErrorKey.NO_PERMISSION_USER));
		}
	}
}