package com.hadenwatne.botcore.service.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class ServerPlayerResultHandler implements AudioLoadResultHandler {
    private ServerPlayer serverPlayer;

    ServerPlayerResultHandler(ServerPlayer player) {
        this.serverPlayer = player;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        serverPlayer.trackLoaded(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        serverPlayer.playlistLoaded(playlist);
    }

    @Override
    public void noMatches() {
        serverPlayer.noMatches();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        serverPlayer.loadFailed(exception);
    }
}
