package com.hadenwatne.shmames.listeners;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.enums.BotSettingName;
import com.hadenwatne.shmames.models.PollModel;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Language;
import com.hadenwatne.shmames.services.ShmamesService;
import com.hadenwatne.shmames.services.TextFormatService;
import com.hadenwatne.shmames.tasks.PollTask;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Date;
import java.util.Timer;

public class PollListener extends ListenerAdapter {
    private PollModel pollModel;

    public PollListener(PollModel pollModel) {
        this.pollModel = pollModel;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if(e.getMessageId().equals(this.pollModel.getMessageID())) {
            if(!e.getUser().isBot()) {
                if (this.pollModel.isActive()) {
                    Message message = e.getChannel().retrieveMessageById(e.getMessageId()).complete();

                    if(e.getReaction().getReactionEmote().getName().equals(TextFormatService.EMOJI_RED_X)) {
                        Brain brain = App.Shmames.getStorageService().getBrain(e.getGuild().getId());

                        // Allow the author OR a user with permission to close the poll. Other reactions will be ignored.
                        if(e.getUserId().equals(this.pollModel.getAuthorID()) || ShmamesService.CheckUserPermission(e.getGuild(), brain.getSettingFor(BotSettingName.POLL_CLOSE), e.getMember())) {
                            Timer t = new Timer();
                            t.schedule(new PollTask(this.pollModel), new Date());
                            App.Shmames.getJDA().removeEventListener(this);
                        }
                    } else {
                        int alphabetNumber = TextFormatService.LetterToNumber(e.getReactionEmote().getName());

                        // Don't bother updating if the reaction isn't an option.
                        if(this.pollModel.getOptions().size() >= alphabetNumber) {
                            Language language = App.Shmames.getLanguageService().getLangFor(message.getGuild());

                            this.pollModel.updateMessageEmbed(language, e.getChannel().getName(), message);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
        if(e.getMessageId().equals(this.pollModel.getMessageID())) {
            if(!e.getUser().isBot()) {
                if (this.pollModel.isActive()) {
                    int alphabetNumber = TextFormatService.LetterToNumber(e.getReactionEmote().getName());

                    // Don't bother updating if the reaction isn't an option.
                    if(this.pollModel.getOptions().size() >= alphabetNumber) {
                        Message message = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
                        Language language = App.Shmames.getLanguageService().getLangFor(message.getGuild());

                        this.pollModel.updateMessageEmbed(language, e.getChannel().getName(), message);
                    }
                }
            }
        }
    }
}
