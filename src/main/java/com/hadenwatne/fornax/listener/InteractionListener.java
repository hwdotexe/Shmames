package com.hadenwatne.fornax.listener;

import com.hadenwatne.fornax.Bot;
import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.IInteractable;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InteractionListener extends ListenerAdapter {
    private final Bot bot;

    public InteractionListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        for (Command command : this.bot.getCommands()) {
            if (command instanceof IInteractable) {
                IInteractable iInteractable = (IInteractable) command;

                for (String id : iInteractable.getInteractionIDs()) {
                    if (id.equalsIgnoreCase(event.getComponentId())) {
                        iInteractable.onButtonClick(event.getInteraction());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        for (Command command : this.bot.getCommands()) {
            if (command instanceof IInteractable) {
                IInteractable iInteractable = (IInteractable) command;

                for (String id : iInteractable.getInteractionIDs()) {
                    if (id.equalsIgnoreCase(event.getComponentId())) {
                        iInteractable.onStringClick(event.getInteraction());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        for (Command command : this.bot.getCommands()) {
            if (command instanceof IInteractable) {
                IInteractable iInteractable = (IInteractable) command;

                for (String id : iInteractable.getInteractionIDs()) {
                    if (id.equalsIgnoreCase(event.getComponentId())) {
                        iInteractable.onEntityClick(event.getInteraction());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        for (Command command : this.bot.getCommands()) {
            if (command instanceof IInteractable) {
                IInteractable iInteractable = (IInteractable) command;

                for (String id : iInteractable.getInteractionIDs()) {
                    if (id.equalsIgnoreCase(event.getModalId())) {
                        iInteractable.onModalSubmit(event.getInteraction());
                        return;
                    }
                }
            }
        }
    }
}