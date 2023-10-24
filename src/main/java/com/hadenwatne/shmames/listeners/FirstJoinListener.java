package com.hadenwatne.shmames.listeners;

import com.hadenwatne.botcore.App;
import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.services.settings.types.BotSettingName;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.botcore.service.types.LogType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.services.settings.BotSetting;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class FirstJoinListener extends ListenerAdapter {
	private final EmbedBuilder welcomeMessage;
	private Shmames shmames;

	public FirstJoinListener(Shmames shmames) {
		this.shmames = shmames;

		EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "Welcome");
		String name = shmames.getBotName();

		embedBuilder.setThumbnail(shmames.getBotAvatarUrl());
		embedBuilder.setDescription("Welcome to "+name+", a memeable utility bot with lots of customization!");

		embedBuilder.addField(":bulb: Get Started", "To view a list of commands and general information, use  `/help`.\n" +
				"\n" +
				"• Adjust settings and permissions with `/modify`\n" +
				"• Play music using `/music`\n" +
				"• Create polls with `/poll`", false);

		embedBuilder.addField(":art: Customization Tips", "• `/trigger` lets you change how you summon "+name+"\n" +
				"• `/response` sends random messages based on a Trigger Type.\n" +
				"• `/family` lets you use "+name+" across multiple Discord servers.\n" +
				"• `/forumweapon` creates shorthand pointers to your favorite GIFs and links.", false);

		embedBuilder.addField(":game_die: Just Have Fun!", "• `/hangman` to play a game of Hangman\n" +
				"• `/cactpot` to get a new Gold Saucer scratch ticket\n" +
				"• `/minesweeper` to try your luck at diffusing bombs", false);

		welcomeMessage = embedBuilder;
	}

	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Brain brain = shmames.getStorageService().getBrain(e.getGuild().getId());

		// Check a setting in the Brain, since this event can fire accidentally if Discord screws up.
		if(!brain.didSendWelcome()){
			TextChannel defaultChannel = e.getGuild().getSystemChannel();

			sendWelcomeMessage(brain, defaultChannel);
			setDefaultChannelSettings(brain, defaultChannel);
		}
	}

	private void sendWelcomeMessage(Brain brain, TextChannel channel) {
		try {
			MessageService.SendMessage(channel, welcomeMessage, false);
		} catch (InsufficientPermissionException e) {
			App.getLogger().Log(LogType.ERROR, "Could not send a welcome message to a server.");
		}

		brain.setSentWelcome();
	}

	private void setDefaultChannelSettings(Brain brain, TextChannel channel) {
		if(channel != null){
			BotSetting pin = brain.getSettingFor(BotSettingName.PIN_CHANNEL);

			pin.setValue(channel.getName(), brain);
		}
	}
}