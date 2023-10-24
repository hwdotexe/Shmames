package com.hadenwatne.shmames.services;

import com.hadenwatne.botcore.service.LoggingService;
import com.hadenwatne.botcore.type.LogType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class MessageService {
    public static Message GetMessageIndicated(ExecutingCommand executingCommand, int count) {
        long latestMessageID = executingCommand.getChannel().getLatestMessageIdLong();
        Message originMessage = executingCommand.hasMessage() ? executingCommand.getMessage() : executingCommand.getChannel().retrieveMessageById(latestMessageID).complete();

        Message indicated = null;
        List<Message> messageHistory;
        int limit = count;

        if (executingCommand.hasInteractionHook()) {
            if (count == 1) {
                indicated = originMessage;
                limit = 0;
            } else {
                limit -= 1;
            }
        }

        if (limit > 0) {
            messageHistory = executingCommand.getChannel().getHistoryBefore(originMessage, limit).complete().getRetrievedHistory();

            // The oldest message in the history we fetched.
            indicated = messageHistory.get(messageHistory.size() - 1);
        }

        return indicated;
    }

    public static void ReplySimpleMessage(Message message, String response, boolean mention) {
        message.reply(response).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(InteractionHook hook, EmbedBuilder response, boolean mention) {
        hook.sendMessageEmbeds(response.build()).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(InteractionHook hook, File file, EmbedBuilder response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file);

        hook.sendFiles(fileUpload).addEmbeds(response.build()).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(InteractionHook hook, InputStream file, String name, EmbedBuilder response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file, name);

        hook.sendFiles(fileUpload).addEmbeds(response.build()).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(InteractionHook hook, EmbedBuilder response, boolean mention, Consumer<? super Message> onSuccess) {
        hook.sendMessageEmbeds(response.build()).mentionRepliedUser(mention).queue(onSuccess, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to interaction hook "+hook.getInteraction().getId()+" in channel "+ hook.getInteraction().getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    public static void ReplyToMessage(Message message, EmbedBuilder response, boolean mention) {
        message.replyEmbeds(response.build()).mentionRepliedUser(mention).queue(success -> {}, error -> {
            MessageService.SendMessage(message.getChannel(), response, false);
        });
    }

    public static void ReplyToMessage(Message message, File file, EmbedBuilder response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file);

        message.replyFiles(fileUpload).setEmbeds(response.build()).mentionRepliedUser(mention).queue(success -> { file.delete(); });
    }

    public static void ReplyToMessage(Message message, InputStream file, String name, EmbedBuilder response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file, name);

        message.replyFiles(fileUpload).setEmbeds(response.build()).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(Message message, EmbedBuilder response, boolean mention, Consumer<? super Message> onSuccess) {
        message.replyEmbeds(response.build()).mentionRepliedUser(mention).queue(onSuccess);
    }

    public static void ReplyToMessage(InteractionHook hook, String response, boolean mention) {
        hook.sendMessage(response).mentionRepliedUser(mention).queue();
    }

    public static void ReplyToMessage(Message message, String response, boolean mention) {
        message.reply(response).mentionRepliedUser(mention).queue(success -> {}, error -> {
            MessageService.SendSimpleMessage(message.getChannel(), response);
        });
    }

    public static void SendDirectMessage(User user, EmbedBuilder message) {
        user.openPrivateChannel().queue(channel -> {
            channel.sendMessageEmbeds(message.build()).queue();
        });
    }

    public static void SendMessage(MessageChannel channel, EmbedBuilder message, boolean mention) {
        channel.sendMessageEmbeds(message.build()).mentionRepliedUser(mention).queue();
    }

    public static void SendMessage(MessageChannel channel, InputStream file, String name, EmbedBuilder message) {
        FileUpload fileUpload = FileUpload.fromData(file, name);

        channel.sendFiles(fileUpload).mentionRepliedUser(false).setEmbeds(message.build()).queue();
    }

    public static Message SendMessageBlocking(MessageChannel channel, EmbedBuilder message) {
        return channel.sendMessageEmbeds(message.build()).mentionRepliedUser(false).complete();
    }

    public static void SendSimpleMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).mentionRepliedUser(false).queue();
    }
}
