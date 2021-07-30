package com.hadenwatne.shmames.models.command;

import jdk.internal.jline.internal.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class ShmamesCommandMessagingChannel {
    private MessageChannel channel;
    private InteractionHook hook;
    private Message originMessage;

    public ShmamesCommandMessagingChannel(Message originMessage, MessageChannel channel) {
        this.channel = channel;
        this.originMessage = originMessage;
    }

    public ShmamesCommandMessagingChannel(InteractionHook hook, MessageChannel channel) {
        this.hook = hook;
        this.channel = channel;
    }

    public boolean hasHook() {
        return this.hook != null;
    }

    public boolean hasOriginMessage() {
        return this.originMessage != null;
    }

    public @Nullable Message getOriginMessage() {
        return this.originMessage;
    }

    public MessageChannel getChannel() {
        return this.channel;
    }

    public @Nullable InteractionHook getHook() {
        return this.hook;
    }
}
