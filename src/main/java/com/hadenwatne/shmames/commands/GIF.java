package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandParameter;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.commandbuilder.ParameterType;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import net.dv8tion.jda.api.entities.*;
import com.hadenwatne.shmames.Utils;

public class GIF implements ICommand {
	private final CommandStructure commandStructure;

	public GIF() {
		this.commandStructure = CommandBuilder.Create("gif")
				.addAlias("who is")
				.addAlias("what is")
				.addParameters(
						new CommandParameter("search", "What to find a GIF for.", ParameterType.STRING)
				)
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String getDescription() {
		return "Send an awesome, randomly-selected GIF based on a search term.";
	}
	
	@Override
	public String getUsage() {
		return this.commandStructure.getUsage();
	}

	@Override
	public String getExamples() {
		return "`gif bill nye`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		MessageChannel channel = data.getMessagingChannel().getChannel();
		String search = data.getArguments().getAsString("search");

		// Obey the channel content settings, if applicable.
		if(channel.getType() == ChannelType.TEXT){
			if(((TextChannel)channel).isNSFW()){
				return Utils.getGIF(search, "low");
			}
		}

		return Utils.getGIF(search, "high");
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
