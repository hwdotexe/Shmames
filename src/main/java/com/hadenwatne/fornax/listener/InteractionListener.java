package com.hadenwatne.fornax.listener;

import com.hadenwatne.fornax.Bot;
import com.hadenwatne.fornax.command.Command;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InteractionListener extends ListenerAdapter {
    private final Bot bot;

    public InteractionListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        for (Command command : this.bot.getCommands()) {
            if (command.getInteractionButtonIDs().contains(event.getComponentId())) {
                String elementID = event.getComponentId().substring(command.getInteractionPrefix().length() - 1);
                command.onInteraction(elementID, event.getInteraction());
                break;
            }
        }
    }
}