package com.hadenwatne.shmames.models.command;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class CommandMessagingChannel {
    private MessageChannel channel;
    private InteractionHook hook;

    public CommandMessagingChannel(MessageChannel channel) {
        this.channel = channel;
    }

    public CommandMessagingChannel(InteractionHook hook) {
        this.hook = hook;
    }

    public boolean hasChannel() {
        return this.channel != null;
    }

    public boolean hasHook() {
        return this.hook != null;
    }

    public MessageChannel getChannel() {
        return this.channel;
    }

    public InteractionHook getHook() {
        return this.hook;
    }
}
