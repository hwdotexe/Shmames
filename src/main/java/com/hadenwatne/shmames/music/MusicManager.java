package com.hadenwatne.shmames.music;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.Shmames;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;

public class MusicManager {
    private final HashMap<String, GuildOcarina> ocarinas;
    private final AudioPlayerManager audioPlayerManager;

    public MusicManager() {
        ocarinas = new HashMap<String, GuildOcarina>();
        audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(getAudioPlayerManager());
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

    public GuildOcarina getOcarina(String guildID) {
        if(ocarinas.containsKey(guildID)) {
            return ocarinas.get(guildID);
        }

        // None found, so let's make one.
        Guild guild = App.Shmames.getJDA().getGuildById(guildID);

        if(guild != null) {
            GuildOcarina ocarina = new GuildOcarina(this, guild.getAudioManager());

            ocarinas.put(guildID, ocarina);
            return ocarina;
        }else{
            return null;
        }
    }

    public void removeGuildOcarina(GuildOcarina guildOcarina) {
        for(String key : ocarinas.keySet()) {
            if(ocarinas.get(key).equals(guildOcarina)){
                ocarinas.remove(key);
                break;
            }
        }
    }
}
