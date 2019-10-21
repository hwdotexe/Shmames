package tech.hadenw.shmamesbot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import tech.hadenw.shmamesbot.AudioPlayerSendHandler;
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
		// TODO: check if playing on this server already, user is in a voice channel, etc.
		
		// Assuming they are in a voice channel.
		VoiceChannel vchannel = message.getMember().getVoiceState().getChannel();
		AudioManager audioManager = message.getGuild().getAudioManager();
		
		// Create a player for this stream
		AudioPlayer player = Shmames.getAudioPlayer().createPlayer();
		
		// This is supposed to schedule things and listen for events
		TrackScheduler trackScheduler = new TrackScheduler(player);
		player.addListener(trackScheduler);
		
		// Hook LavaPlayer handler into JDA.
		// DO NOT have multiple of these per guild (TODO)
		audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
		
		// Play it, baby!
		audioManager.openAudioConnection(vchannel);
		
		// Not sure what this does.
		String identifier = "https://youtube.com/watch?v=dQw4w9WgXcQ";
		
		// Might want to make AudioLoadResultHandler a separate class?
		Shmames.getAudioPlayer().loadItem(identifier, new AudioLoadResultHandler() {
			  @Override
			  public void trackLoaded(AudioTrack track) {
			    trackScheduler.queue(track);
			  }

			  @Override
			  public void playlistLoaded(AudioPlaylist playlist) {
			    for (AudioTrack track : playlist.getTracks()) {
			      trackScheduler.queue(track);
			    }
			  }

			  @Override
			  public void noMatches() {
				  message.getChannel().sendMessage("No matches!").queue();
			  }

			  @Override
			  public void loadFailed(FriendlyException throwable) {
			    // Notify the user that everything exploded
				  message.getChannel().sendMessage("Everything Exploded:\n"+throwable.getMessage()).queue();
			  }
		});
		
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
