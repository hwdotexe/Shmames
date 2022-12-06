package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

public class MessageService {

    /**
     * Retrieves a Message indicated by the user. Aware of slash commands & traditional commands.
     * @param executingCommand The command context running.
     * @param count The number of messages above to retrieve.
     * @return A Message, if possible.
     */
    public static Message GetMessageIndicated(ExecutingCommand executingCommand, int count) {
        try {
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
        } catch (PermissionException pe) {
            return null;
        }
    }

    /**
     * Replies to a message with a simple unformatted string.
     * @param message The message to reply to.
     * @param response The response to send.
     * @param mention Whether to mention the author.
     */
    public static void ReplySimpleMessage(Message message, String response, boolean mention) {
        message.reply(response).mentionRepliedUser(mention).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to message "+message.getId()+" in channel "+ message.getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    /**
     * A centralized place to send message replies to Discord, and to handle issues when they arise.
     * @param hook The InteractionHook to reply to.
     * @param response The response to send.
     */
    public static void ReplyToMessage(InteractionHook hook, EmbedBuilder response, boolean mention) {
        hook.sendMessageEmbeds(response.build()).mentionRepliedUser(mention).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to interaction hook "+hook.getInteraction().getId()+" in channel "+ hook.getInteraction().getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    /**
     * Replies to a message and embeds a file - typically an image - into the response.
     * @param hook The message to reply to.
     * @param file The file to send.
     * @param response The embed to include with this file.
     */
    public static void ReplyToMessage(InteractionHook hook, File file, EmbedBuilder response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file);

        hook.sendFiles(fileUpload).addEmbeds(response.build()).mentionRepliedUser(mention).queue(success -> { file.delete(); }, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to interaction hook "+hook.getInteraction().getId()+" in channel "+ hook.getInteraction().getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    /**
     * Replies to a message and embeds a file - typically an image - into the response.
     * @param hook The message to reply to.
     * @param file The file to send.
     * @param name The name of the file to send.
     * @param response The embed to include with this file.
     */
    public static void ReplyToMessage(InteractionHook hook, InputStream file, String name, EmbedBuilder response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file, name);

        hook.sendFiles(fileUpload).addEmbeds(response.build()).mentionRepliedUser(mention).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to interaction hook "+hook.getInteraction().getId()+" in channel "+ hook.getInteraction().getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    /**
     * Replies to the message and accepts a Consumer to handle the success value when the message is sent.
     * @param hook The InteractionHook to reply to.
     * @param response The response to send.
     * @param onSuccess The Consumer action to take if successful.
     */
    public static void ReplyToMessage(InteractionHook hook, EmbedBuilder response, boolean mention, Consumer<? super Message> onSuccess) {
        hook.sendMessageEmbeds(response.build()).mentionRepliedUser(mention).queue(onSuccess, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to interaction hook "+hook.getInteraction().getId()+" in channel "+ hook.getInteraction().getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    /**
     * A centralized place to send message replies to Discord, and to handle issues when they arise.
     * @param message The message to reply to.
     * @param response The response to send.
     */
    public static void ReplyToMessage(Message message, EmbedBuilder response, boolean mention) {
        message.replyEmbeds(response.build()).mentionRepliedUser(mention).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to message "+message.getId()+" in channel "+ message.getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    /**
     * Replies to a message and embeds a file - typically an image - into the response.
     * @param message The message to reply to.
     * @param file The file to send.
     * @param response The embed to include with this file.
     */
    public static void ReplyToMessage(Message message, File file, EmbedBuilder response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file);

        message.replyFiles(fileUpload).setEmbeds(response.build()).mentionRepliedUser(mention).queue(success -> { file.delete(); }, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to message "+message.getId()+" in channel "+ message.getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    /**
     * Replies to a message and embeds a file - typically an image - into the response.
     * @param message The message to reply to.
     * @param file The file to send.
     * @param name The name of the file to send.
     * @param response The embed to include with this file.
     */
    public static void ReplyToMessage(Message message, InputStream file, String name, EmbedBuilder response, boolean mention) {
        FileUpload fileUpload = FileUpload.fromData(file, name);

        message.replyFiles(fileUpload).setEmbeds(response.build()).mentionRepliedUser(mention).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to message "+message.getId()+" in channel "+ message.getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    /**
     * Replies to the message and accepts a Consumer to handle the success value when the message is sent.
     * @param message The Message to reply to.
     * @param response The response to send.
     * @param mention Whether to mention the author.
     * @param onSuccess The Consumer action to take if successful.
     */
    public static void ReplyToMessage(Message message, EmbedBuilder response, boolean mention, Consumer<? super Message> onSuccess) {
        message.replyEmbeds(response.build()).mentionRepliedUser(mention).queue(onSuccess, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to message "+message.getId()+" in channel "+ message.getChannel().getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    /**
     * Sends the provided message to the user via Direct Message.
     * @param user The user to send a Direct Message to.
     * @param message The message to send.
     */
    public static void SendDirectMessage(User user, EmbedBuilder message) {
        user.openPrivateChannel().queue(channel -> {
            channel.sendMessageEmbeds(message.build()).queue();
        });
    }

    /**
     * Sends a message in a channel without replying - ideal for messages that were not prompted by a user or command.
     * @param channel The channel to send a message in.
     * @param message The message to send.
     */
    public static void SendMessage(MessageChannel channel, EmbedBuilder message, boolean mention) {
        channel.sendMessageEmbeds(message.build()).mentionRepliedUser(mention).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not send a message in channel "+channel.getId());
        });
    }

    /**
     * Sends a message in a channel without replying - ideal for messages that were not prompted by a user or command.
     * @param channel The channel to send a message in.
     * @param file The file to send.
     * @param name The name of the file to send.
     * @param message The message to send.
     */
    public static void SendMessage(MessageChannel channel, InputStream file, String name, EmbedBuilder message) {
        FileUpload fileUpload = FileUpload.fromData(file, name);

        channel.sendFiles(fileUpload).mentionRepliedUser(false).setEmbeds(message.build()).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not send a message in channel "+channel.getId());
            LoggingService.Log(LogType.ERROR, error.getMessage());
        });
    }

    /**
     * Sends a message in a channel without replying - ideal for messages that were not prompted by a user or command.
     * This option blocks the thread until the message is sent.
     * @param channel The channel to send a message in.
     * @param message The message to send.
     */
    public static Message SendMessageBlocking(MessageChannel channel, EmbedBuilder message) {
        return channel.sendMessageEmbeds(message.build()).mentionRepliedUser(false).complete();
    }

    /**
     * Sends a message in a channel without replying and without any embed information.
     * @param channel The channel to send a message in.
     * @param message The message to send.
     */
    public static void SendSimpleMessage(MessageChannel channel, String message) {
        channel.sendMessage(message).mentionRepliedUser(false).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not send a message in channel "+channel.getId());
        });
    }
}
