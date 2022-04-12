package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.models.command.ShmamesCommandMessagingChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.List;

public class MessageService {
    /**
     * Retrieves a Message indicated by the user. Aware of slash commands & traditional commands.
     * @param messagingChannel The channel data to use.
     * @param count The number of messages above to retrieve.
     * @return A Message, if possible.
     */
    public static Message GetMessageIndicated(ShmamesCommandMessagingChannel messagingChannel, int count) {
        try {
            long latestMessageID = messagingChannel.getChannel().getLatestMessageIdLong();
            Message originMessage = messagingChannel.hasOriginMessage() ? messagingChannel.getOriginMessage() : messagingChannel.getChannel().retrieveMessageById(latestMessageID).complete();

            Message indicated = null;
            List<Message> messageHistory;
            int limit = count;

            if (messagingChannel.hasHook()) {
                if (count == 1) {
                    indicated = originMessage;
                    limit = 0;
                } else {
                    limit -= 1;
                }
            }

            if (limit > 0) {
                messageHistory = messagingChannel.getChannel().getHistoryBefore(originMessage, limit).complete().getRetrievedHistory();

                // The oldest message in the history we fetched.
                indicated = messageHistory.get(messageHistory.size() - 1);
            }

            return indicated;
        } catch (PermissionException pe) {
            return null;
        }
    }
}
