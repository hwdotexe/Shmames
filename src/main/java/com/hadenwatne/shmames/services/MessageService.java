package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class MessageService {
    /**
     * Sends a message in a channel without replying - ideal for messages that were not prompted by a user or command.
     * @param channel The channel to send a message in.
     * @param message The message to send.
     */
    public static void SendMessage(MessageChannel channel, EmbedBuilder message) {
        channel.sendMessageEmbeds(message.build()).queue(success -> {}, error -> {
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
        channel.sendFile(file, name).setEmbeds(message.build()).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not send a message in channel "+channel.getId());
        });
    }

    /**
     * Replies to a message and embeds a file - typically an image - into the response.
     * @param message The message to reply to.
     * @param file The file to send.
     * @param response The embed to include with this file.
     */
    public static void ReplyToMessage(Message message, File file, EmbedBuilder response) {
        message.reply(file).setEmbeds(response.build()).queue(success -> { file.delete(); }, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to message "+message.getId()+" in server "+ message.getGuild().getId());
        });
    }

    /**
     * Replies to a message and embeds a file - typically an image - into the response.
     * @param hook The message to reply to.
     * @param file The file to send.
     * @param response The embed to include with this file.
     */
    public static void ReplyToMessage(InteractionHook hook, File file, EmbedBuilder response) {
        hook.sendFile(file).addEmbeds(response.build()).queue(success -> { file.delete(); }, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to interaction hook "+hook.getInteraction().getId()+" in server "+ hook.getInteraction().getGuild().getId());
        });
    }

    /**
     * Replies to a message and embeds a file - typically an image - into the response.
     * @param message The message to reply to.
     * @param file The file to send.
     * @param name The name of the file to send.
     * @param response The embed to include with this file.
     */
    public static void ReplyToMessage(Message message, InputStream file, String name, EmbedBuilder response) {
        message.reply(file, name).setEmbeds(response.build()).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to message "+message.getId()+" in server "+ message.getGuild().getId());
        });
    }

    /**
     * Replies to a message and embeds a file - typically an image - into the response.
     * @param hook The message to reply to.
     * @param file The file to send.
     * @param name The name of the file to send.
     * @param response The embed to include with this file.
     */
    public static void ReplyToMessage(InteractionHook hook, InputStream file, String name, EmbedBuilder response) {
        hook.sendFile(file, name).addEmbeds(response.build()).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to interaction hook "+hook.getInteraction().getId()+" in server "+ hook.getInteraction().getGuild().getId());
        });
    }

    /**
     * A centralized place to send message replies to Discord, and to handle issues when they arise.
     * @param message The message to reply to.
     * @param response The response to send.
     */
    public static void ReplyToMessage(Message message, EmbedBuilder response) {
        message.replyEmbeds(response.build()).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to message "+message.getId()+" in server "+ message.getGuild().getId());
        });
    }

    /**
     * A centralized place to send message replies to Discord, and to handle issues when they arise.
     * @param hook The InteractionHook to reply to.
     * @param response The response to send.
     */
    public static void ReplyToMessage(InteractionHook hook, EmbedBuilder response) {
        hook.sendMessageEmbeds(response.build()).queue(success -> {}, error -> {
            LoggingService.Log(LogType.ERROR, "Could not reply to interaction hook "+hook.getInteraction().getId()+" in server "+ hook.getInteraction().getGuild().getId());
        });
    }

    /**
     * A shorthand method for sending a basic reply with minimal styling.
     * @param message The message to reply to.
     * @param type The type of response to create.
     * @param header The navigation header for this response.
     * @param body The body of the response.
     */
    public static void ReplyToMessage(Message message, EmbedType type, String header, String body) {
        EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(type, header);
        embedBuilder.addField(null, body, false);

        ReplyToMessage(message, embedBuilder);
    }

    /**
     * A shorthand method for sending a basic reply with minimal styling.
     * @param hook The InteractionHook to reply to.
     * @param type The type of response to create.
     * @param header The navigation header for this response.
     * @param body The body of the response.
     */
    public static void ReplyToMessage(InteractionHook hook, EmbedType type, String header, String body) {
        EmbedBuilder embedBuilder = EmbedFactory.GetEmbed(type, header);
        embedBuilder.addField(null, body, false);

        ReplyToMessage(hook, embedBuilder);
    }

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
}
