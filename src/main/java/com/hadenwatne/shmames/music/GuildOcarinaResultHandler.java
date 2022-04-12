package com.hadenwatne.shmames.music;

import com.hadenwatne.shmames.services.LoggingService;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class GuildOcarinaResultHandler implements AudioLoadResultHandler {
    private GuildOcarina ocarina;
    private boolean addToQueue;
    private boolean isLoadingPlaylist;
    private boolean doSkip;
    private int addedToPlaylist;

    public GuildOcarinaResultHandler(GuildOcarina o) {
        ocarina = o;
        addToQueue = false;
        isLoadingPlaylist = false;
        addedToPlaylist = 0;
        doSkip = false;
    }

    public void prepareLoadingTrack(boolean addToQueue) {
        this.addToQueue = addToQueue;
        this.isLoadingPlaylist = false;
        this.doSkip = !addToQueue;
    }

    public void prepareLoadingPlaylist(boolean addToQueue) {
        this.addToQueue = addToQueue;
        this.isLoadingPlaylist = true;
        this.addedToPlaylist = 0;
        this.doSkip = !addToQueue;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if(addToQueue) {
            ocarina.getQueue().add(track);
        } else {
            if(isLoadingPlaylist) {
                ocarina.getQueue().add(Math.min(addedToPlaylist, ocarina.getQueue().size()), track);

                if(!doSkip) {
                    addedToPlaylist++;
                }
            }else{
                ocarina.getQueue().add(0, track);
            }
        }

        if(ocarina.getNowPlaying() == null || doSkip) {
            doSkip = false;
            ocarina.skip();
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        for(AudioTrack t : playlist.getTracks()) {
            trackLoaded(t);
        }
    }

    @Override
    public void noMatches() {
        ocarina.sendMessageToChannel("A track was queued, but returned 0 matches.");
    }

    @Override
    public void loadFailed(FriendlyException throwable) {
        ocarina.sendMessageToChannel("Loading failed for a track.");
        LoggingService.LogException(throwable);

        if(ocarina.getNowPlaying() == null && ocarina.getQueue().size() == 0) {
            ocarina.stop();
        }
    }
}
