package tech.hadenw.shmamesbot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.entities.MessageChannel;

public class TrackScheduler extends AudioEventAdapter implements AudioLoadResultHandler  {
	
	private AudioPlayer player;
	private MessageChannel channel;
	
	public TrackScheduler(AudioPlayer p, MessageChannel c) {
		player = p;
		channel = c;
	}
	
	@Override
	  public void trackLoaded(AudioTrack track) {
	    this.queue(track);
	  }

	  @Override
	  public void playlistLoaded(AudioPlaylist playlist) {
	    for (AudioTrack track : playlist.getTracks()) {
	      this.queue(track);
	    }
	  }

	  @Override
	  public void noMatches() {
		  channel.sendMessage("No matches!").queue();
	  }

	  @Override
	  public void loadFailed(FriendlyException throwable) {
		  channel.sendMessage("Everything Exploded:\n"+throwable.getMessage()).queue();
	  }
	
	// TODO make this an actual queue sometime.
	public void queue(AudioTrack track) {
		player.playTrack(track);
	}
	
	  @Override
	  public void onPlayerPause(AudioPlayer player) {
	    // Player was paused
	  }

	  @Override
	  public void onPlayerResume(AudioPlayer player) {
	    // Player was resumed
	  }

	  @Override
	  public void onTrackStart(AudioPlayer player, AudioTrack track) {
	    // A track started playing
	  }

	  @Override
	  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
	    if (endReason.mayStartNext) {
	      // Start next track
	    }

	    // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
	    // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
	    // endReason == STOPPED: The player was stopped.
	    // endReason == REPLACED: Another track started playing while this had not finished
	    // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
	    //                       clone of this back to your queue
	  }

	  @Override
	  public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
	    // An already playing track threw an exception (track end event will still be received separately)
	  }

	  @Override
	  public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
	    // Audio track has been unable to provide us any audio, might want to just start a new track
	  }
}
