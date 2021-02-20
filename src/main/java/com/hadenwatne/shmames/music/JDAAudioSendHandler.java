package com.hadenwatne.shmames.music;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

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

	  public boolean canProvide() {
	    lastFrame = audioPlayer.provide();
	    return lastFrame != null;
	  }

	  public ByteBuffer provide20MsAudio() {
	    return ByteBuffer.wrap(lastFrame.getData());
	  }

	  public boolean isOpus() {
	    return true;
	  }
}