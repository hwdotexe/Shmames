package com.hadenwatne.shmames;

import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class BotUpgradeMessage {
	final EmbedBuilder upgradeMessage;

	public BotUpgradeMessage() {
		String name = App.Shmames.getBotName();
		EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(EmbedType.INFO, "v2.0.0 Upgrade");

		embedBuilder.setThumbnail(App.Shmames.getBotAvatarUrl());

		embedBuilder.addField(":wave: Hello, everyone!", name + " has been hard at work learning some new tricks and forgetting some old ones. After an extensive rework of commands, cosmetics, and backend code, we're ready to launch **" + name + " v2.0.0!**", false);

		embedBuilder.addField(":loudspeaker: What's New?", "• Completely redesigned bot responses\n" +
				"• Quality of life improvements\n" +
				"• Better command structures for certain commands\n" +
				"• Reworked bot permissions\n" +
				"• ... and a ton more!", false);

		embedBuilder.addField(":information_source: Next Steps", "With all these changes, server admins will need to **re-configure their /modify settings**, as some settings have changed and were reset to their defaults.", false);
		embedBuilder.addField(":white_check_mark: List of Changes", "You can [read the full changelog](https://raw.githubusercontent.com/hwdotexe/Shmames/master/changelog.txt) for more details and a complete list of changes.", false);

		upgradeMessage = embedBuilder;

		// Send the message to every guild this bot is a member of.
		for(Guild guild : App.Shmames.getJDA().getGuilds()) {
			Brain brain = App.Shmames.getStorageService().getBrain(guild.getId());

			if(!brain.didSendUpgrade()){
				BaseGuildMessageChannel defaultChannel = guild.getDefaultChannel();

				sendUpgradeMessage(brain, defaultChannel);
			}
		}
	}

	private void sendUpgradeMessage(Brain brain, BaseGuildMessageChannel channel) {
		try {
			MessageService.SendMessage(channel, upgradeMessage, false);
		} catch (InsufficientPermissionException e) {
			LoggingService.Log(LogType.ERROR, "Could not send an upgrade message to a server.");
		}

		brain.setSentUpgrade();
	}
}