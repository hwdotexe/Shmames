package com.hadenwatne.shmames.commands;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.shmames.App;
import com.hadenwatne.fornax.command.builder.CommandBuilder;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.language.LanguageKey;
import com.hadenwatne.shmames.models.Story;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import com.hadenwatne.shmames.services.PaginationService;
import com.hadenwatne.shmames.services.RandomService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

public class Storytime extends Command {
	public Storytime() {
		super(false);
	}

	@Override
	protected Permission[] configureRequiredBotPermissions() {
		return new Permission[]{Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS, Permission.MESSAGE_EMBED_LINKS};
	}

	@Override
	protected CommandStructure buildCommandStructure() {
		return CommandBuilder.Create("storytime", "I tell you a high-quality story.")
				.build();
	}

	@Override
	public EmbedBuilder run (ExecutingCommand executingCommand) {
		List<Story> stories = App.Shmames.getStorageService().getBrainController().getStories().getStories();
		Story randomStory = stories.get(RandomService.GetRandom(stories.size()));
		List<String> storyText = PaginationService.SplitString(randomStory.getText(), MessageEmbed.VALUE_MAX_LENGTH);
		EmbedBuilder embed = response(EmbedType.INFO)
				.setDescription(executingCommand.getLanguage().getMsg(LanguageKey.STORY_INTRO));

		embed.addField(randomStory.getTitle(), "_by "+randomStory.getAuthor()+"_", false);

		for(String s : storyText) {
			embed.addField("", s, false);
		}

		return embed;
	}
}
