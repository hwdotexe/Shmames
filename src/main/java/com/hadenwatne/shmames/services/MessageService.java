package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.enums.EmbedType;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.factories.EmbedFactory;
import com.hadenwatne.shmames.models.command.ExecutingCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.List;

public class MessageService {
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
