package com.hadenwatne.discordbot.listeners;

import com.hadenwatne.discordbot.storage.*;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.hadenwatne.discordbot.Shmames;

import java.util.List;

public class FirstJoinListener extends ListenerAdapter {

	public FirstJoinListener(){

	}

	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());

		// Check a setting in the Brain, since this event can fire accidentally if Discord screws up.
		if(!b.didSendWelcome()){
			// Try to set default values for this specific guild.
			try{
				List<TextChannel> general = e.getGuild().getTextChannelsByName("general", true);

				if(general.size() > 0){
					BotSetting pin = b.getSettingFor(BotSettingName.PIN_CHANNEL);
					BotSetting dev = b.getSettingFor(BotSettingName.DEV_ANNOUNCE_CHANNEL);
					BotSetting rem = b.getSettingFor(BotSettingName.REMOVAL_EMOTE);
					BotSetting app = b.getSettingFor(BotSettingName.APPROVAL_EMOTE);

					pin.setValue(general.get(0).getName(), b);
					dev.setValue(general.get(0).getName(), b);

					List<Emote> em = e.getGuild().getEmotes();

					if(em.size() > 0)
						rem.setValue(em.get(0).getName(), b);

					if(em.size() > 1)
						app.setValue(em.get(1).getName(), b);
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
				"settings, and tweak the values you want to change:```Use \"`"+name+" modify`\" to get started, and \"`"+name+" help`\" for a list of commands.").queue();
	}
}