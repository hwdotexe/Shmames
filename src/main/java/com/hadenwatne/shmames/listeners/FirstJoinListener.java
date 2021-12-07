package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.ShmamesLogger;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.data.BotSetting;
import com.hadenwatne.shmames.models.data.Brain;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.hadenwatne.shmames.Shmames;

import java.util.List;

public class FirstJoinListener extends ListenerAdapter {
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());

		// Check a setting in the Brain, since this event can fire accidentally if Discord screws up.
		if(!b.didSendWelcome()){
			// Try to set default values for this specific guild.
			try{
				TextChannel defaultChannel = e.getGuild().getDefaultChannel();

				if(defaultChannel != null){
					BotSetting pin = b.getSettingFor(BotSettingName.PIN_CHANNEL);

					pin.setValue(defaultChannel.getName(), b);
				}
			}catch (Exception ex){
				ShmamesLogger.log(LogType.ERROR, "Shmames could not set default bot settings.");
			}

			// Try to send a welcome message.
			try{
				sendMessage(e.getGuild().getDefaultChannel());
				b.setSentWelcome();
			}catch (Exception ex){
				ShmamesLogger.log(LogType.ERROR, "Shmames could not send a welcome message.");
			}
		}
	}

	private void sendMessage(TextChannel tc){
		String name = Shmames.getBotName();

		tc.sendMessage("``` __ _                                         \n" +
				"/ _\\ |__  _ __ ___   __ _ _ __ ___   ___  ___ \n" +
				"\\ \\| '_ \\| '_ ` _ \\ / _` | '_ ` _ \\ / _ \\/ __|\n" +
				"_\\ \\ | | | | | | | | (_| | | | | | |  __/\\__ \\\n" +
				"\\__/_| |_|_| |_| |_|\\__,_|_| |_| |_|\\___||___/\n" +
				"\n" +
				"Welcome to "+name+" - the Discord bot that's changing\n" +
				"the world. Thank you for installing me!\n" +
				"\n" +
				"For first-time setup, please take a look at the bot's\n" +
				"settings, and tweak the values you want to change.\n" +
				"• Choose the roles allowed to play music and create polls.\n" +
				"• Decide which emoji should be used to \"approve\" or \"remove\" a message.\n" +
				"• Add a custom alias to summon the bot.\n" +
				"\n" +
				"Some interesting commands to get you started:\n" +
				"• "+name+" help\n" +
				"• "+name+" modify\n" +
				"• "+name+" music\n" +
				"\n" +
				"Hint: you can use my name to run a command in chat, or you can use a Slash Command (/)." +
				"```").queue();
	}
}