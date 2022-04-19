package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.HTTPService;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class GIF extends Command {
	public GIF() {
		super(false);
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("gif", "Send an awesome, randomly-selected GIF based on a search term.")
				.addAlias("who is")
				.addAlias("what is")
				.addParameters(
						new CommandParameter("search", "What to find a GIF for.", ParameterType.STRING)
								.setExample("bob ross")
				)
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		String search = executingCommand.getCommandArguments().getAsString("search");

		// Obey the channel content settings, if applicable.
		MessageChannel channel = executingCommand.getChannel();
		String gif = "";

		if(channel.getType() == ChannelType.TEXT){
			if(((TextChannel)channel).isNSFW()){
				gif = HTTPService.GetGIF(search, "low");
			}
		}

		if(gif.length() == 0) {
			gif = HTTPService.GetGIF(search, "high");
		}

		try {
			EmbedBuilder response = response(EmbedType.INFO);
			InputStream file = new URL(gif).openStream();

			response.setImage("attachment://result.gif");
			response.setDescription(search);
			executingCommand.replyFile(file, "result.gif", response);

			return null;
		} catch (Exception e) {
			LoggingService.LogException(e);

			return response(EmbedType.ERROR, Errors.BOT_ERROR.name())
					.setDescription(executingCommand.getLanguage().getError(Errors.BOT_ERROR));
		}
	}
}
