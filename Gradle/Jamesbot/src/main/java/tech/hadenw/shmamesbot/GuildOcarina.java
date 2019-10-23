package tech.hadenw.shmamesbot;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

// A Guild-specific object created when music has been requested.
// One per server.
public class GuildOcarina extends AudioEventAdapter implements AudioLoadResultHandler  {
	
	private AudioPlayer player;
	private AudioManager manager;
	
	public GuildOcarina(AudioManager am) {
		player = Shmames.getAudioPlayer().createPlayer();
		player.addListener(this);
		manager = am;
		
		if(manager.getSendingHandler() != null)
			((JDAAudioSendHandler)manager.getSendingHandler()).setAudioPlayer(player);
		else
			manager.setSendingHandler(new JDAAudioSendHandler(player));
	}
	
	public void connect(VoiceChannel vc) {
		// TODO drop other connections first
		// TODO check join permissions
		manager.openAudioConnection(vc);
	}
	
	public void queueTrack(String item) {
		Shmames.getAudioPlayer().loadItem(item, this);
	}
	
	public void togglePause() {
		player.setPaused(!player.isPaused());
	}
	
	public void stopPlaying() {
		if(manager.isConnected()) {
			player.stopTrack();
			//player.destroy();
			manager.closeAudioConnection();
		}
	}
	
	// TODO make this an actual queue sometime.
	private void queue(AudioTrack track) {
		player.playTrack(track);
	}
	
	// AudioLoadResultHandler
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
		//channel.sendMessage("No matches!").queue();
		System.out.println("No matches");
	}

	@Override
	public void loadFailed(FriendlyException throwable) {
		//channel.sendMessage("Everything Exploded:\n"+throwable.getMessage()).queue();
		System.out.println("Load failed");
	}
	
	// AudioEventAdapter
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
	    } else {
	    	//this.stopPlaying();
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
