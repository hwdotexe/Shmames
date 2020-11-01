package com.hadenwatne.discordbot.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hadenwatne.discordbot.Shmames;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nullable;

// A Guild-specific object created when music has been requested.
// One per server.
public class GuildOcarina extends AudioEventAdapter {

	private MusicManager musicManager;
	private AudioPlayer player;
	private AudioManager manager;
	private List<AudioTrack> queue;
	private TextChannel msgChannel;
	private GuildOcarinaResultHandler loader;
	private boolean isLoop;
	private boolean isLoopQueue;

	public GuildOcarina(MusicManager mm, AudioManager am) {
		musicManager = mm;
		player = Shmames.getMusicManager().getAudioPlayerManager().createPlayer();
		player.addListener(this);
		manager = am;
		queue = new ArrayList<AudioTrack>();
		loader = new GuildOcarinaResultHandler(this);
		isLoop = false;
		isLoopQueue = false;

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
	
	public void connect(VoiceChannel vc, TextChannel ch) {
		msgChannel = ch;
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

	public boolean toggleLoopQueue() {
		isLoopQueue = !isLoopQueue;

		return isLoopQueue;
	}

	public boolean isLooping() {
		return isLoop;
	}

	public boolean isLoopingQueue() {
		return isLoopQueue;
	}

	public void shuffleQueue() {
		Collections.shuffle(queue);
	}

	public void loadTrack(String url, boolean addToQueue) {
		loader.prepareLoadingTrack(addToQueue);
		Shmames.getMusicManager().getAudioPlayerManager().loadItem(url, loader);
	}

	public void loadCustomPlaylist(List<String> urls, boolean addToQueue) {
		long order = System.currentTimeMillis();

		loader.prepareLoadingPlaylist(addToQueue);

		for(String url : urls) {
			Shmames.getMusicManager().getAudioPlayerManager().loadItemOrdered(order, url, loader);
		}
	}
	
	public void togglePause(boolean paused) {
		player.setPaused(paused);
	}

	public boolean isPaused() {
		return player.isPaused();
	}
	
	public void skip(){
		if(queue.size()>0) {
			player.playTrack(queue.get(0));
			queue.remove(0);
		} else {
			stop();
		}
	}

	public void sendMessageToChannel(String msg) {
		if(msg != null && msg.length() > 0) {
			msgChannel.sendMessage(msg).queue();
		}
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
				// Only append to queue if regular looping is off.
				// Otherwise we'll be adding a lot of copies to the queue ._.
				if(isLoopQueue && !isLoop) {
					queue.add(track.makeClone());
				}

				if(isLoop) {
					player.startTrack(track.makeClone(), false);
//					loadTrack(track.getInfo().uri, false);
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
		System.out.println("Track stuck: "+track.getInfo().title);
	}
}
