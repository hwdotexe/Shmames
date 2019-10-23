package tech.hadenw.shmamesbot;

import java.util.ArrayList;
import java.util.List;

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
	private List<AudioTrack> queue;
	private boolean isPlaying;
	
	public GuildOcarina(AudioManager am) {
		player = Shmames.getAudioPlayer().createPlayer();
		player.addListener(this);
		manager = am;
		queue = new ArrayList<AudioTrack>();
		isPlaying = false;
		
		if(manager.getSendingHandler() != null)
			((JDAAudioSendHandler)manager.getSendingHandler()).setAudioPlayer(player);
		else
			manager.setSendingHandler(new JDAAudioSendHandler(player));
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public List<AudioTrack> getQueue(){
		return queue;
	}
	
	public AudioPlayer getPlayer() {
		return player;
	}
	
	public void connect(VoiceChannel vc) {
		this.disconnect();
		
		manager.openAudioConnection(vc);
	}
	
	public void disconnect() {
		if(manager.isConnected()) {
			player.stopTrack();
			manager.closeAudioConnection();
			queue.clear();
			isPlaying = false;
		}
	}
	
	public void loadTrack(String item) {
		Shmames.getAudioPlayer().loadItem(item, this);
	}
	
	public void togglePause() {
		player.setPaused(!player.isPaused());
	}
	
	public void skipTrack(){
		player.playTrack(queue.get(0));
		queue.remove(0);
	}
	
	//
	//AudioLoadResultHandler
	//
	
	@Override
	public void trackLoaded(AudioTrack track) {
		queue.add(track);
		
		if(!isPlaying) {
			player.playTrack(track);
			isPlaying = true;
		}
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		for (AudioTrack track : playlist.getTracks()) {
			queue.add(track);
	    }
		
		if(!isPlaying) {
			player.playTrack(queue.get(0));
			isPlaying = true;
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
	
	//
	// AudioEventAdapter
	//
	
	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		// A track started playing
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if(endReason.mayStartNext) {
			player.playTrack(queue.get(0));
			queue.remove(0);
		}else {
			this.disconnect();
		}
	}
	
	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		// Audio track has been unable to provide us any audio, might want to just start a new track
	}
}
