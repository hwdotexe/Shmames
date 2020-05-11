package tech.hadenw.discordbot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import tech.hadenw.discordbot.CommandHandler;
import tech.hadenw.discordbot.Errors;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.Utils;
import tech.hadenw.discordbot.commands.ICommand;
import tech.hadenw.discordbot.storage.BotSettingName;
import tech.hadenw.discordbot.storage.Brain;

import java.util.ArrayList;
import java.util.List;

public class FirstJoinListener extends ListenerAdapter {

	public FirstJoinListener(){

	}

	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Brain b = Shmames.getBrains().getBrain(e.getGuild().getId());

		// Check a setting in the Brain, since this event can fire accidentally if Discord screws up.
		if(!b.didSendWelcome()){

			try{
				sendMessage(e.getGuild().getDefaultChannel());
				b.setSentWelcome();
			}catch (Exception ex){
				System.out.println("Shmames could not send a welcome message.");
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