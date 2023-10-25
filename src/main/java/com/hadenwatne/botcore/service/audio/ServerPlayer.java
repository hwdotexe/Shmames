package com.hadenwatne.botcore.service.audio;

import com.hadenwatne.shmames.music.JDAAudioSendHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class ServerPlayer extends AudioEventAdapter {
    private AudioPlayerManager serverAudioPlayerManager;
    private AudioManager serverAudioManager;
    private AudioPlayer audioPlayer;
    private ServerPlayerResultHandler resultHandler;

    ServerPlayer(AudioPlayerManager audioPlayerManager, AudioManager audioManager) {
        this.serverAudioPlayerManager = audioPlayerManager;
        this.serverAudioManager = audioManager;
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.resultHandler = new ServerPlayerResultHandler(this);

        this.audioPlayer.addListener(this);

        if (audioManager.getSendingHandler() != null) {
            ((JDAAudioSendHandler) audioManager.getSendingHandler()).setAudioPlayer(this.audioPlayer);
        } else {
            audioManager.setSendingHandler(new JDAAudioSendHandler(this.audioPlayer));
        }
    }
}
