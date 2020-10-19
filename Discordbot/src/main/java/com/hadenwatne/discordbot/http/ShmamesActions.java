package com.hadenwatne.discordbot.http;

import com.hadenwatne.discordbot.Shmames;
import net.dv8tion.jda.api.entities.Activity;

public class ShmamesActions {
    public static void SetTempStatus(Activity.ActivityType type, String string) {
        Shmames.getJDA().getPresence().setActivity(Activity.of(type, string));
    }
}
