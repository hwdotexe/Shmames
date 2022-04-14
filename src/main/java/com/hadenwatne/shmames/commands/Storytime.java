package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.PaginationService;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

public class Storytime implements ICommand {
	private final CommandStructure commandStructure;

	public Storytime() {
		this.commandStructure = CommandBuilder.Create("storytime", "I tell you a high-quality story.")
				.build();
	}

	@Override
	public CommandStructure getCommandStructure() {
		return this.commandStructure;
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		List<String> stories = App.Shmames.getStorageService().getBrainController().getStories().getStories();
		String randomStory = stories.get(RandomService.GetRandom(stories.size()));
		List<String> story = PaginationService.SplitString(randomStory, MessageEmbed.VALUE_MAX_LENGTH);
		EmbedBuilder embed = new EmbedBuilder();

		embed.setTitle("Let's read a story!");
		embed.setColor(Color.PINK);

		for(String s : story) {
			embed.addField("", s, false);
		}

		data.getMessagingChannel().sendMessage(embed);

		return "";
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
