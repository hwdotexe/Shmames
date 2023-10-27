package com.hadenwatne.shmames.music;

import com.hadenwatne.shmames.App;
import com.hadenwatne.botcore.service.types.LogType;
import com.hadenwatne.botcore.service.LoggingService;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// A Guild-specific object created when music has been requested.
// One per server.
public class GuildOcarina extends AudioEventAdapter {

	private MusicManager musicManager;
	private AudioPlayer player;
	private AudioManager manager;
	private List<AudioTrack> queue;
	private MessageChannel msgChannel;
	private GuildOcarinaResultHandler loader;
	private boolean isLoop;
	private boolean isLoopQueue;
	private HashMap<String, Integer> timecodes;

	private final Pattern youtubeTimecode = Pattern.compile("\\?t=(\\d+)$", Pattern.CASE_INSENSITIVE);
	private final Pattern youtubeIdentifier = Pattern.compile("((((.be)|(.com))\\/)|(v=))([a-z0-9_\\-]+)", Pattern.CASE_INSENSITIVE);

	public GuildOcarina(MusicManager mm, AudioManager am) {
		musicManager = mm;
		player = App.Shmames.getMusicManager().getAudioPlayerManager().createPlayer();
		player.addListener(this);
		manager = am;
		queue = new ArrayList<AudioTrack>();
		loader = new GuildOcarinaResultHandler(this);
		isLoop = false;
		isLoopQueue = false;
		timecodes = new HashMap<>();

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
	
	public void connect(AudioChannel vc, MessageChannel ch) {
		msgChannel = ch;
		manager.openAudioConnection(vc);
		manager.setSelfDeafened(true);
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

	public void reverseQueue() {
		Collections.reverse(queue);
	}

	public void loadTrack(String url, boolean addToQueue) {
		loader.prepareLoadingTrack(addToQueue);
		App.Shmames.getMusicManager().getAudioPlayerManager().loadItem(url, loader);
		checkAddYouTubeTimecode(url);
	}

	public void loadCustomPlaylist(List<String> urls, boolean addToQueue) {
		long order = System.currentTimeMillis();

		loader.prepareLoadingPlaylist(addToQueue);

		for(String url : urls) {
			App.Shmames.getMusicManager().getAudioPlayerManager().loadItemOrdered(order, url, loader);
			checkAddYouTubeTimecode(url);
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

	public void skipMany(int count){
		if(queue.size() >= count) {
			player.playTrack(queue.get(count-1));

			for(int i=0; i<count; i++) {
				queue.remove(0);
			}
		} else {
			stop();
		}
	}

	public void sendMessageToChannel(String msg) {
		if(msg != null && msg.length() > 0) {
			msgChannel.sendMessage(msg).queue();
		}
	}

	private void checkAddYouTubeTimecode(String url) {
		Matcher timecodeMatch = youtubeTimecode.matcher(url);
		Matcher identifierMatch = youtubeIdentifier.matcher(url);

		if(timecodeMatch.find() && identifierMatch.find()){
			timecodes.put(identifierMatch.group(7), Integer.parseInt(timecodeMatch.group(1)));
		}
	}
	
	//
	// AudioEventAdapter
	//
	
	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		if(timecodes.containsKey(track.getInfo().identifier)){
			int seconds = timecodes.get(track.getInfo().identifier);

			track.setPosition(seconds*1000);

			timecodes.remove(track.getInfo().identifier);
		}
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
					track.
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
		LoggingService.Log(LogType.ERROR, "A music track is stuck: "+track.getInfo().title);
		this.skip();
	}
}
