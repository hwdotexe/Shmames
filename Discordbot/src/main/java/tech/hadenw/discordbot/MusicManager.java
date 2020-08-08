package tech.hadenw.discordbot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;

/**
 *
 */
public class MusicManager {
    private HashMap<String, GuildOcarina> ocarinas;
    private AudioPlayerManager audioPlayerManager;

    public MusicManager() {
        ocarinas = new HashMap<String, GuildOcarina>();
        audioPlayerManager = new DefaultAudioPlayerManager();
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }

    public GuildOcarina getOcarina(String guildID) {
        if(ocarinas.containsKey(guildID)) {
            return ocarinas.get(guildID);
        }

        // None found, so let's make one.
        Guild guild = Shmames.getJDA().getGuildById(guildID);

        if(guild != null) {
            GuildOcarina ocarina = new GuildOcarina(guild.getAudioManager());

            ocarinas.put(guildID, ocarina);
            return ocarina;
        }else{
            return null;
        }
    }
}
