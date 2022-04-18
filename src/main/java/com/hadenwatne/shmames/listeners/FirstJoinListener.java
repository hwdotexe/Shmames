package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.data.BotSetting;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.hadenwatne.shmames.Shmames;

public class FirstJoinListener extends ListenerAdapter {
	final EmbedBuilder welcomeMessage;

	public FirstJoinListener() {
		EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "Welcome");
		String name = App.Shmames.getBotName();

		embedBuilder.setThumbnail(App.Shmames.getBotAvatarUrl());
		embedBuilder.setDescription("Welcome to "+name+", a memeable utility bot with lots of customization!");

		embedBuilder.addField("Get Started", "To view a list of commands and general information, use `"+name+" help`.\n" +
				"\n" +
				"• Adjust settings and permissions with `"+name+" modify`\n" +
				"• Play music using `"+name+" music`\n" +
				"• Create polls with `"+name+" poll`\n" +
				"_... and a whole lot more!_", false);

		embedBuilder.addField("Customization Tips", "• **Triggers** let you change how you summon "+name+"\n" +
				"• **Responses** are messages sent at random based on a Trigger Type.\n" +
				"• **Families** let you use "+name+" across multiple Discord servers.\n" +
				"• **Tallies** can keep track of things that are important to you.\n" +
				"• **Forum Weapons** are shorthand pointers to your favorite GIFs and links.", false);

		embedBuilder.addField("Just Have Fun!", "• `"+name+" hangman` to play a game of Hangman\n" +
				"• `"+name+" cactpot` to get a new Gold Saucer scratch ticket\n" +
				"• `"+name+" minesweeper` to try your luck at diffusing bombs", false);

		welcomeMessage = embedBuilder;
	}

	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Brain brain = App.Shmames.getStorageService().getBrain(e.getGuild().getId());

		// Check a setting in the Brain, since this event can fire accidentally if Discord screws up.
		if(!brain.didSendWelcome()){
			BaseGuildMessageChannel defaultChannel = e.getGuild().getDefaultChannel();

			sendWelcomeMessage(brain, defaultChannel);
			setDefaultChannelSettings(brain, defaultChannel);
		}
	}

	private void sendWelcomeMessage(Brain brain, BaseGuildMessageChannel channel) {
		try {
			MessageService.SendMessage(channel, welcomeMessage);
		} catch (InsufficientPermissionException e) {
			LoggingService.Log(LogType.ERROR, "Could not send a welcome mesage to a server.");
		}

		brain.setSentWelcome();
	}

	private void setDefaultChannelSettings(Brain brain, BaseGuildMessageChannel channel) {
		if(channel != null){
			BotSetting pin = brain.getSettingFor(BotSettingName.PIN_CHANNEL);

			pin.setValue(channel.getName(), brain);
		}
	}
}