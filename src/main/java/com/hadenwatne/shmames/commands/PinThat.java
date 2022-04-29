package com.hadenwatne.shmames.commands;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.MessageService;
import com.hadenwatne.shmames.services.TextFormatService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.BotSettingName;

public class PinThat extends Command {
	public PinThat() {
		super(true);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("pinthat", "Sends a copy of the specified message over to the Pin Channel, if configured.")
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

		try {
			Message toPin = MessageService.GetMessageIndicated(executingCommand, messages);
			TextChannel channelToSendPin = executingCommand.getServer().getTextChannelById(executingCommand.getBrain().getSettingFor(BotSettingName.POLL_PIN_CHANNEL).getAsString());

			if(channelToSendPin != null) {
				EmbedBuilder response = response(EmbedType.INFO);
				InputStream file = new URL(toPin.getAuthor().getEffectiveAvatarUrl()).openStream();
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
						.setDescription("Pinned!");
			}else{
				return response(EmbedType.ERROR, Errors.CHANNEL_NOT_FOUND.name())
						.setDescription(executingCommand.getLanguage().getError(Errors.CHANNEL_NOT_FOUND));
			}
		} catch (NumberFormatException e) {
			LoggingService.LogException(e);

			return response(EmbedType.ERROR, Errors.CHANNEL_NOT_FOUND.name())
					.setDescription(executingCommand.getLanguage().getError(Errors.CHANNEL_NOT_FOUND));
		} catch (Exception e) {
			LoggingService.LogException(e);

			return response(EmbedType.ERROR, Errors.NO_PERMISSION_BOT.name())
					.setDescription(executingCommand.getLanguage().getError(Errors.NO_PERMISSION_BOT));
		}
	}
}