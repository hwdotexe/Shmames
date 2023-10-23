package com.hadenwatne.botcore.utility;

import com.hadenwatne.botcore.App;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class BotUtility {
    public static JDA authenticate(String apiKey) {
        try {
            return JDABuilder.createDefault(apiKey)
                    .enableCache(CacheFlag.EMOJI)
                    .build();
        } catch (InvalidTokenException e) {
            App.getLogger().LogException(e);
        }

        return null;
    }
}
