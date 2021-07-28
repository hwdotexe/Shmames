package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.Shmames;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter {

    //https://github.com/DV8FromTheWorld/JDA/blob/development/src/examples/java/SlashBotExample.java
    @Override
    public void onSlashCommand(SlashCommandEvent event)
    {
        Shmames.getCommandHandler().PerformCommand(event, event.getChannel(), event.getUser(), event.getGuild());
    }
}
