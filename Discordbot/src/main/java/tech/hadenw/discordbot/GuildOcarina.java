package tech.hadenw.discordbot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nullable;

// A Guild-specific object created when music has been requested.
// One per server.
public class GuildOcarina extends AudioEventAdapter implements AudioLoadResultHandler  {
	
	private AudioPlayer player;
	private AudioManager manager;
	private List<AudioTrack> queue;
	private boolean isLoop;
	
	public GuildOcarina(AudioManager am) {
		player = Shmames.getMusicManager().getAudioPlayerManager().createPlayer();
		player.addListener(this);
		manager = am;
		queue = new ArrayList<AudioTrack>();
		isLoop = false;
		
		if(manager.getSendingHandler() != null) {
			((JDAAudioSendHandler) manager.getSendingHandler()).setAudioPlayer(player);
		} else {
			manager.setSendingHandler(new JDAAudioSendHandler(player));
		}
	}
	
	public boolean isInVoiceChannel() {
		return manager.isConnected();
	}
	
	public List<AudioTrack> getQueue(){
		return queue;
	}
	
	public void connect(VoiceChannel vc) {
		this.stop();
		
		manager.openAudioConnection(vc);
	}
	
	public void stop() {
		if(isInVoiceChannel()) {
			player.stopTrack();
			manager.closeAudioConnection();
			queue.clear();
		}
	}

	@Nullable
	public AudioTrack getNowPlaying() {
		return player.getPlayingTrack();
	}

	public boolean toggleLoop() {
		isLoop = !isLoop;

		return isLoop;
	}

	public void shuffleQueue() {
		Collections.shuffle(queue);
	}

	public void queueItem(String item) {
		Shmames.getMusicManager().getAudioPlayerManager().loadItem(item, this);
	}
	
	public void togglePause() {
		player.setPaused(!player.isPaused());
	}
	
	public void skip(){
		if(queue.size()>0) {
			isLoop = false;
			player.playTrack(queue.get(0));
			queue.remove(0);
		}
	}
	
	//
	//AudioLoadResultHandler
	//

	@Override
	public void trackLoaded(AudioTrack track) {
		queue.add(track);

		if(this.player.getPlayingTrack() == null) {
			this.skip();
		}
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		queue.addAll(playlist.getTracks());

		// If there isn't a playing track, play the next in queue.
		if(this.player.getPlayingTrack() == null)
			this.skip();
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
		System.out.println("Track started");
	}

	// This might get called when the user tries to skip a song.
	// This should cover song looping, disconnecting if empty queue,
	// etc.
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		System.out.println("Track ended due to "+endReason+": "+track.getInfo().title);

		if(endReason.mayStartNext) {
			if(track.getPosition() == track.getDuration()){
				// Song ended naturally. Loop or skip?
				if(isLoop) {
					player.playTrack(track);
				}else{
					if(queue.size() > 0){
						this.skip();
					}else{
						this.stop();
					}
				}
			}else{
				// Probably got skipped. Do nothing if there are additional songs.
				if(queue.size() == 0){
					stop();
				}
			}
		}else{
			this.stop();
		}
	}
	
	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		// Audio track has been unable to provide us any audio, might want to just start a new track
		System.out.println("Track stuck");
	}
}
