package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.data.BotSetting;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class FirstJoinListener extends ListenerAdapter {
	final EmbedBuilder welcomeMessage;

	public FirstJoinListener() {
		EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "Welcome");
		String name = App.Shmames.getBotName();

		embedBuilder.setThumbnail(App.Shmames.getBotAvatarUrl());
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
		Brain brain = App.Shmames.getStorageService().getBrain(e.getGuild().getId());

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
			LoggingService.Log(LogType.ERROR, "Could not send a welcome message to a server.");
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