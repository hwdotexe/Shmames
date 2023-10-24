package com.hadenwatne.shmames.commands;

import com.hadenwatne.botcore.command.Command;
import com.hadenwatne.botcore.command.builder.CommandBuilder;
import com.hadenwatne.botcore.command.builder.CommandParameter;
import com.hadenwatne.botcore.command.builder.CommandStructure;
import com.hadenwatne.botcore.command.builder.types.ParameterType;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.HTTPService;
import com.hadenwatne.botcore.service.LoggingService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class GIF extends Command {
	public GIF() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_ATTACH_FILES};
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
		} catch (IOException e) {
			LoggingService.LogException(e);

			return response(EmbedType.ERROR, ErrorKeys.BOT_ERROR.name())
					.setDescription(executingCommand.getLanguage().getError(ErrorKeys.BOT_ERROR));
		}
	}
}
