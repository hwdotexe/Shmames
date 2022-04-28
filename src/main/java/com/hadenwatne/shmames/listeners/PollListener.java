package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.TextFormatService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class PollListener extends ListenerAdapter {
    private PollModel poll;

    public PollListener(PollModel pollModel) {
        this.poll = pollModel;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if(e.getMessageId().equals(this.poll.getMessageID())) {
            if(!e.getUser().isBot()) {
                if (this.poll.isActive()) {
                    Message message = e.getChannel().retrieveMessageById(e.getMessageId()).complete();

                    this.updatePollEmbed(message, e.getChannel().getName());
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
        if(e.getMessageId().equals(this.poll.getMessageID())) {
            if(!e.getUser().isBot()) {
                if (this.poll.isActive()) {
                    Message message = e.getChannel().retrieveMessageById(e.getMessageId()).complete();

                    this.updatePollEmbed(message, e.getChannel().getName());
                }
            }
        }
    }

    private void updatePollEmbed(Message message, String channelName) {
        Brain brain = App.Shmames.getStorageService().getBrain(message.getGuild().getId());
        Lang lang = App.Shmames.getLanguageService().getLangFor(brain);

        // Count the votes and update the embed.
        HashMap<Integer, Integer> votes = new HashMap<>();
        int totalVotes = 0;

        // Count the votes.
        for (MessageReaction r : message.getReactions()) {
            int voteOption = TextFormatService.LetterToNumber(r.getReactionEmote().getName());

            if (voteOption > 0) {
                // Remove 1 because the bot adds a default reaction.
                votes.put(voteOption, r.getCount() - 1);
                totalVotes++;
            }
        }

        // Build votes field.
        EmbedBuilder embedBuilder = this.poll.buildMessageEmbed(lang, channelName, this.poll.getExpiration(), false);
        embedBuilder.addField(this.poll.buildVoteField(votes, totalVotes));

        message.editMessageEmbeds(embedBuilder.build()).queue();
    }
}
