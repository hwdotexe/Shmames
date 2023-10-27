package com.hadenwatne.botcore.service.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;

public class AudioService {
    private final HashMap<Guild, ServerPlayer> players;
    private final AudioPlayerManager manager;

    public AudioService() {
        this.players = new HashMap<>();
        this.manager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.manager);
    }

    @Deprecated
    public AudioPlayerManager getManager() {
        return this.manager;
    }

    public ServerPlayer getServerPlayer(Guild guild) {
        return this.players.getOrDefault(guild, null);
    }

    public ServerPlayer createServerPlayer(Guild guild) {
        ServerPlayer player = new ServerPlayer(this, this.manager, guild.getAudioManager());

        this.players.put(guild, player);

        return player;
    }

    public void removeServerPlayer(ServerPlayer player) {
        for (Guild key : this.players.keySet()) {
            if (this.players.get(key).equals(player)) {
                removeServerPlayer(key);
                break;
            }
        }
    }

    public void removeServerPlayer(Guild guild) {
        this.players.remove(guild);
    }
}
