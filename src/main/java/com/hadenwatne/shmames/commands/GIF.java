package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.HTTPService;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class GIF implements ICommand {
	private final CommandStructure commandStructure;

	public GIF() {
		this.commandStructure = CommandBuilder.Create("gif", "Send an awesome, randomly-selected GIF based on a search term.")
				.addAlias("who is")
				.addAlias("what is")
				.addParameters(
						new CommandParameter("search", "What to find a GIF for.", ParameterType.STRING)
				)
				.setExample("gif bob ross")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		MessageChannel channel = data.getMessagingChannel().getChannel();
		String search = data.getArguments().getAsString("search");

		// Obey the channel content settings, if applicable.
		if(channel.getType() == ChannelType.TEXT){
			if(((TextChannel)channel).isNSFW()){
				return HTTPService.GetGIF(search, "low");
			}
		}

		String gif = HTTPService.GetGIF(search, "high");

		if (data.getMessagingChannel().hasHook()) {
			return "> _" + search + "_\n" + gif;
		}

		return gif;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
