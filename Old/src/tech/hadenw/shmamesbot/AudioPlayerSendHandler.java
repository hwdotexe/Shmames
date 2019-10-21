package tech.hadenw.shmamesbot;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.core.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {
	  private final AudioPlayer audioPlayer;
	  private AudioFrame lastFrame;

	  public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
	    this.audioPlayer = audioPlayer;
	  }

	  @Override
	  public boolean canProvide() {
	    lastFrame = audioPlayer.provide();
	    return lastFrame != null;
	  }

	  @Override
	  public byte[] provide20MsAudio() {
	    return ByteBuffer.wrap(lastFrame.getData()).array();
	  }

	  @Override
	  public boolean isOpus() {
	    return true;
	  }
}