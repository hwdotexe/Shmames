package com.hadenwatne.botcore.service.audio;

import com.hadenwatne.botcore.App;
import com.hadenwatne.shmames.music.JDAAudioSendHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerPlayer extends AudioEventAdapter {
    private AudioService audioService;
    private AudioPlayerManager serverAudioPlayerManager;
    private AudioManager serverAudioManager;
    private AudioPlayer audioPlayer;
    private ServerPlayerResultHandler resultHandler;
    private GuildMessageChannelUnion activatedChannel;
    private List<AudioTrack> queue;
    private List<AudioTrack> history;
    private IAudioListener customListener;
    private boolean loopTrack;
    private boolean loopQueue;

    ServerPlayer(AudioService audioService, AudioPlayerManager audioPlayerManager, AudioManager audioManager) {
        this.audioService = audioService;
        this.serverAudioPlayerManager = audioPlayerManager;
        this.serverAudioManager = audioManager;
        this.audioPlayer = audioPlayerManager.createPlayer();
        this.resultHandler = new ServerPlayerResultHandler(this);
        this.queue = new ArrayList<>();
        this.history = new ArrayList<>();
        this.loopTrack = false;
        this.loopQueue = false;

        this.audioPlayer.addListener(this);

        if (audioManager.getSendingHandler() != null) {
            ((JDAAudioSendHandler) audioManager.getSendingHandler()).setAudioPlayer(this.audioPlayer);
        } else {
            audioManager.setSendingHandler(new JDAAudioSendHandler(this.audioPlayer));
        }
    }

    public void registerCustomListener(IAudioListener listener) {
        this.customListener = listener;
    }

    public boolean isInVoiceChannel() {
        return serverAudioManager.isConnected();
    }

    public boolean isPaused() {
        return audioPlayer.isPaused();
    }

    public boolean isTrackLooping() {
        return loopTrack;
    }

    public boolean isQueueLooping() {
        return loopQueue;
    }

    public List<AudioTrack> getQueue() {
        return queue;
    }

    public List<AudioTrack> getHistory() {
        return history;
    }

    public AudioTrack getNowPlaying() {
        return audioPlayer.getPlayingTrack();
    }

    public void shuffleQueue() {
        Collections.shuffle(queue);
    }

    public void reverseQueue() {
        Collections.reverse(queue);
    }

    public void togglePause(boolean paused) {
        audioPlayer.setPaused(paused);
    }

    public void setTrackLooping(boolean looping) {
        this.loopTrack = looping;
    }

    public void setQueueLooping(boolean looping) {
        this.loopQueue = looping;
    }

    public int getVolume() {
        return this.audioPlayer.getVolume();
    }

    public void setVolume(int volume) {
        this.audioPlayer.setVolume(volume);
    }

    public void connect(AudioChannel vc, GuildMessageChannelUnion ch) {
        activatedChannel = ch;
        serverAudioManager.openAudioConnection(vc);
        serverAudioManager.setSelfDeafened(true);
    }

    public void disconnect() {
        if (isInVoiceChannel()) {
            audioPlayer.stopTrack();
            serverAudioManager.closeAudioConnection();
            queue.clear();
            history.clear();
            audioService.removeServerPlayer(serverAudioManager.getGuild());
        }
    }

    public void load(String url) {
        serverAudioPlayerManager.loadItem(url, resultHandler);
    }

    public void loadMany(List<String> urls) {
        long order = System.currentTimeMillis();

        for (String url : urls) {
            serverAudioPlayerManager.loadItemOrdered(order, url, resultHandler);
        }
    }

    public void play(AudioTrack track) {
        audioPlayer.playTrack(track);
    }

    public void skip() {
        if (!queue.isEmpty()) {
            AudioTrack track = queue.get(0);

            play(track);
            queue.remove(0);
            history.add(track);
        } else {
            disconnect();
        }
    }

    public void skipMany(int count) {
        if (queue.size() >= count) {
            if (count > 0) {
                history.addAll(queue.subList(0, count));
                queue.subList(0, count).clear();
            }

            play(queue.get(0));
        } else {
            disconnect();
        }
    }

    public void seekTo(int seconds) {
        if (audioPlayer.getPlayingTrack() != null && audioPlayer.getPlayingTrack().isSeekable()) {
            audioPlayer.getPlayingTrack().setPosition(seconds * 1000L);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (serverAudioManager.isConnected()) {
            if (serverAudioManager.getConnectedChannel().getMembers().size() == 1) {
                this.disconnect();
                return;
            }

            if (endReason.mayStartNext) {
                history.add(track);

                if (loopQueue && !loopTrack) {
                    queue.add(track.makeClone());
                } else if (loopTrack) {
                    queue.add(0, track.makeClone());
                }

                skip();
            } else {
                disconnect();
            }
        }
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        skip();
    }

    void trackLoaded(AudioTrack track) {
        if (this.customListener == null) {
            if (this.audioPlayer.getPlayingTrack() == null) {
                play(track);
            } else {
                this.queue.add(track);
            }
        } else {
            this.customListener.trackLoaded(track);
        }
    }

    void playlistLoaded(AudioPlaylist playlist) {
        if (this.customListener == null) {
            this.queue.addAll(playlist.getTracks());

            if (this.audioPlayer.getPlayingTrack() == null) {
                skip();
            }
        } else {
            this.customListener.playlistLoaded(playlist);
        }
    }

    void noMatches() {
        if (this.customListener != null) {
            this.customListener.noMatches();
        }
    }

    void loadFailed(FriendlyException exception) {
        if (this.customListener == null) {
            App.getLogger().LogException(exception);

            if (this.audioPlayer.getPlayingTrack() == null && this.queue.isEmpty()) {
                disconnect();
            }
        } else {
            this.customListener.loadFailed(exception);
        }
    }
}