package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.commandbuilder.CommandBuilder;
import com.hadenwatne.shmames.commandbuilder.CommandStructure;
import com.hadenwatne.shmames.models.command.ShmamesCommandData;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
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
	public String getExamples() {
		return "`storytime`";
	}

	@Override
	public String run (Lang lang, Brain brain, ShmamesCommandData data) {
		List<String> stories = Shmames.getBrains().getStories().getStories();
		String randomStory = stories.get(Utils.getRandom(stories.size()));
		List<String> story = Utils.splitString(randomStory, MessageEmbed.VALUE_MAX_LENGTH);
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
