package tech.hadenw.shmamesbot;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.core.audio.AudioSendHandler;

public class JDAAudioSendHandler implements AudioSendHandler {
	  private AudioPlayer audioPlayer;
	  private AudioFrame lastFrame;

	  public JDAAudioSendHandler(AudioPlayer audioPlayer) {
	    this.audioPlayer = audioPlayer;
	  }
	  
	  public void setAudioPlayer(AudioPlayer audioPlayer) {
		  // Destroy the old player and enable the new one.
		  this.audioPlayer.destroy();
		  
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