package com.hadenwatne.shmames.models.command;

import jdk.internal.jline.internal.Nullable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class ShmamesCommandMessagingChannel {
    private MessageChannel channel;
    private InteractionHook hook;
    private Message originMessage;
    private boolean hasSentMessage;

    public ShmamesCommandMessagingChannel(Message originMessage, MessageChannel channel) {
        this.channel = channel;
        this.originMessage = originMessage;
        this.hasSentMessage = false;
    }

    public ShmamesCommandMessagingChannel(InteractionHook hook, MessageChannel channel) {
        this.hook = hook;
        this.channel = channel;
        this.hasSentMessage = false;
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

    public boolean hasSentMessage() {
        return this.hasSentMessage;
    }

    public void sendMessage(String message) {
        if(hasHook()) {
            this.hook.sendMessage(message).queue();
        } else {
            this.channel.sendMessage(message).queue();
        }

        this.hasSentMessage = true;
    }

    public void sendMessage(EmbedBuilder embedBuilder) {
        if(hasHook()) {
            this.hook.sendMessageEmbeds(embedBuilder.build()).queue();
        } else {
            this.channel.sendMessageEmbeds(embedBuilder.build()).queue();
        }

        this.hasSentMessage = true;
    }
}
