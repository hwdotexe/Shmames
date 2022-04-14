package com.hadenwatne.shmames.commands;

import java.awt.Color;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.BotSettingName;

public class PinThat implements ICommand {
	private final CommandStructure commandStructure;

	public PinThat() {
		this.commandStructure = CommandBuilder.Create("pinthat", "Sends a copy of the specified message over to the Pin Channel, if configured.")
				.addParameters(
						new CommandParameter("position", "A number of carats (^) pointing to the message", ParameterType.STRING)
								.setPattern("([\\^]{1,15})")
				)
				.setExample("pinthat ^^^")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run(Lang lang, Brain brain, ShmamesCommandData data) {
		int messages = data.getArguments().getAsString("position").length();

		try {
			Message toPin = MessageService.GetMessageIndicated(data.getMessagingChannel(), messages);

			boolean channelFound = false;
			for (TextChannel ch : data.getServer().getTextChannels()) {
				if (ch.getId().equalsIgnoreCase(brain.getSettingFor(BotSettingName.PIN_CHANNEL).getValue())) {
					channelFound = true;

					EmbedBuilder eBuilder = new EmbedBuilder();

					eBuilder.setAuthor(toPin.getAuthor().getName(), null, toPin.getAuthor().getEffectiveAvatarUrl());
					eBuilder.setColor(Color.CYAN);

					StringBuilder msg = new StringBuilder(toPin.getContentRaw());
					for (Attachment a : toPin.getAttachments()) {
						msg.append("\n");
						msg.append(a.getUrl());
					}
					eBuilder.appendDescription(msg.toString());
					eBuilder.setFooter("#" + toPin.getChannel().getName() + " - Pinned by @" + data.getAuthor().getName(), null);

					MessageEmbed embed = eBuilder.build();
					ch.sendMessageEmbeds(embed).queue();

					break;
				}
			}

			if (!channelFound)
				return lang.getError(Errors.CHANNEL_NOT_FOUND, true);

			return "";
		} catch (Exception ex) {
			ex.printStackTrace();
			return lang.getError(Errors.NO_PERMISSION_BOT, true);
		}
	}

	@Override
	public boolean requiresGuild() {
		return true;
	}
}