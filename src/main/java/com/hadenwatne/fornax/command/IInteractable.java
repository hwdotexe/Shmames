package com.hadenwatne.fornax.command;

import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

public interface IInteractable {
    public String[] getInteractionIDs();
    public void onButtonClick(ButtonInteraction buttonInteraction);
    public void onStringClick(StringSelectInteraction stringSelectInteraction);
    public void onEntityClick(EntitySelectInteraction entitySelectInteraction);
    public void onModalSubmit(ModalInteraction modalInteraction);
}
