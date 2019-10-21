package tech.hadenw.shmamesbot.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import tech.hadenw.shmamesbot.JDAAudioSendHandler;
import tech.hadenw.shmamesbot.Errors;
import tech.hadenw.shmamesbot.Shmames;
import tech.hadenw.shmamesbot.TrackScheduler;

public class Music implements ICommand {
	@Override
	public String getDescription() {
		return "Play music to your current voice channel.";
	}
	
	@Override
	public String getUsage() {
		return "music <play|pause|stop|queue> [link] [move <from> <to>|clear]";
	}

	@Override
	public String run(String args, User author, Message message) {
		Matcher m = Pattern.compile("^(play|pause|stop|queue)\\w?(.+)?$").matcher(args.toLowerCase());
		
		if(m.find()) {
			switch(m.group(1).toLowerCase()) {
			case "play":
				return "Under construction";
			case "pause":
				return "Under construction";
			case "stop":
				return "Under construction";
			case "queue":
				return "Under construction";
			}
		}else {
			return Errors.formatUsage(Errors.WRONG_USAGE, this.getUsage());
		}
		// TODO: check if playing on this server already, user is in a voice channel, etc.
		
		// Assuming they are in a voice channel.
		VoiceChannel vchannel = message.getMember().getVoiceState().getChannel();
		AudioManager audioManager = message.getGuild().getAudioManager();
		
		// Create a player for this stream
		AudioPlayer player = Shmames.getAudioPlayer().createPlayer();
		
		// This is supposed to schedule things and listen for events
		TrackScheduler trackScheduler = new TrackScheduler(player, message.getChannel());
		player.addListener(trackScheduler);
		
		// Hook LavaPlayer handler into JDA.
		// DO NOT have multiple of these per guild
		if(audioManager.getSendingHandler() != null)
			((JDAAudioSendHandler)audioManager.getSendingHandler()).setAudioPlayer(player);
		else
			audioManager.setSendingHandler(new JDAAudioSendHandler(player));
		
		// Play it, baby!
		audioManager.openAudioConnection(vchannel);
		
		// Load and play the requested item.
		String item = "https://youtube.com/watch?v=dQw4w9WgXcQ";
		Shmames.getAudioPlayer().loadItem(item, trackScheduler);
		
		return "It is done";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"music", "bops"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
