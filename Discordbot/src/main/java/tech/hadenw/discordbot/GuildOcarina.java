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

	private MusicManager musicManager;
	private AudioPlayer player;
	private AudioManager manager;
	private List<AudioTrack> queue;
	private boolean isLoop;
	private boolean queueNextTrack;
	
	public GuildOcarina(MusicManager mm, AudioManager am) {
		musicManager = mm;
		player = Shmames.getMusicManager().getAudioPlayerManager().createPlayer();
		player.addListener(this);
		manager = am;
		queue = new ArrayList<AudioTrack>();
		isLoop = false;
		queueNextTrack = false;
		
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
			musicManager.removeGuildOcarina(this);
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

	public boolean isLooping() {
		return isLoop;
	}

	public void shuffleQueue() {
		Collections.shuffle(queue);
	}

	public void loadTrack(String url, boolean addToQueue) {
		queueNextTrack = addToQueue;
		Shmames.getMusicManager().getAudioPlayerManager().loadItem(url, this);
	}

	public void loadTrackOrdered(String url, long order, boolean addToQueue) {
		queueNextTrack = addToQueue;
		Shmames.getMusicManager().getAudioPlayerManager().loadItemOrdered(order, url, this);
	}
	
	public void togglePause() {
		player.setPaused(!player.isPaused());
	}
	
	public void skip(){
		if(queue.size()>0) {
			player.playTrack(queue.get(0));
			queue.remove(0);
		} else {
			stop();
		}
	}
	
	//
	//AudioLoadResultHandler
	//

	@Override
	public void trackLoaded(AudioTrack track) {
		if(queueNextTrack){
			queue.add(track);

			if(this.player.getPlayingTrack() == null) {
				this.skip();
			}
		} else {
			queue.add(0, track);
			this.skip();
		}
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		if(queueNextTrack){
			queue.addAll(playlist.getTracks());

			if(this.player.getPlayingTrack() == null) {
				this.skip();
			}
		} else {
			queue.addAll(0,playlist.getTracks());
			this.skip();
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

	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		// Leave the channel if nobody's here.
		if(manager.getConnectedChannel().getMembers().size() == 1) {
			this.stop();
			return;
		}

		switch(endReason) {
			case STOPPED:
				if(queue.size() == 0) {
					stop();
				}

				break;
			case FINISHED:
				if(isLoop) {
					loadTrack(track.getInfo().uri, false);
				} else {
					if(queue.size() > 0){
						this.skip();
					}else{
						this.stop();
					}
				}

				break;
			default:
				break;
		}
	}
	
	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		// Audio track has been unable to provide us any audio, might want to just start a new track
		System.out.println("Track stuck");
	}
}
