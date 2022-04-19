package com.hadenwatne.shmames.models.command;

import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.models.data.Brain;
import com.hadenwatne.shmames.models.data.Lang;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.MessageService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.InteractionHook;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;

public class ExecutingCommand {
    private final Lang language;
    private final @Nullable Brain brain;

    private String commandName;
    private String subCommandGroup;
    private String subCommand;
    private ExecutingCommandArguments commandArguments;
    private InteractionHook hook;
    private Message message;

    public ExecutingCommand(Lang language, @Nullable Brain brain) {
        this.language = language;
        this.brain = brain;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public Lang getLanguage() {
        return this.language;
    }

    public @Nullable Brain getBrain() {
        return this.brain;
    }

    public @Nullable String getSubCommandGroup() {
        return this.subCommandGroup;
    }

    public void setSubCommandGroup(String subCommandGroup) {
        this.subCommandGroup = subCommandGroup;
    }

    public boolean hasSubCommandGroup() {
        return this.subCommandGroup != null;
    }

    public @Nullable String getSubCommand() {
        return this.subCommand;
    }

    public void setSubCommand(String subCommand) {
        this.subCommand = subCommand;
    }

    public boolean hasSubCommand() {
        return this.subCommand != null;
    }

    public @Nullable ExecutingCommandArguments getCommandArguments() {
        return this.commandArguments;
    }

    public void setCommandArguments(ExecutingCommandArguments commandArguments) {
        this.commandArguments = commandArguments;
    }

    public boolean hasCommandArguments() {
        return this.commandArguments != null;
    }

    public void setInteractionHook(InteractionHook hook) {
        this.hook = hook;
    }

    public Message getMessage() {
        return this.message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public boolean hasMessage() {
        return this.message != null;
    }

    public boolean hasInteractionHook() {
        return this.hook != null;
    }

    public User getAuthorUser() {
        if(hook != null) {
            return hook.getInteraction().getUser();
        } else if(message != null) {
            return message.getAuthor();
        } else {
            return null;
        }
    }

    public Member getAuthorMember() {
        if(hook != null) {
            return hook.getInteraction().getMember();
        } else if(message != null) {
            return message.getMember();
        } else {
            return null;
        }
    }

    public Guild getServer() {
        if(hook != null) {
            return hook.getInteraction().isFromGuild() ? hook.getInteraction().getGuild() : null;
        } else if(message != null) {
            return message.isFromGuild() ? message.getGuild() : null;
        } else {
            return null;
        }
    }

    public MessageChannel getChannel() {
        if(hook != null) {
            return hook.getInteraction().getMessageChannel();
        } else if(message != null) {
            return message.getChannel();
        } else {
            return null;
        }
    }

    public void reply(EmbedBuilder embedBuilder) {
        if(embedBuilder != null) {
            if (hook != null) {
                MessageService.ReplyToMessage(hook, embedBuilder);
            } else if (message != null) {
                MessageService.ReplyToMessage(message, embedBuilder);
            } else {
                LoggingService.Log(LogType.ERROR, "Could not send response for command " + this.commandName);
            }
        }
    }

    public void replyFile(InputStream file, String name, EmbedBuilder embedBuilder) {
        if(embedBuilder != null && file != null) {
            if (hook != null) {
                MessageService.ReplyToMessage(hook, file, name, embedBuilder);
            } else if (message != null) {
                MessageService.ReplyToMessage(message, file, name, embedBuilder);
            } else {
                LoggingService.Log(LogType.ERROR, "Could not send response for command " + this.commandName);
            }
        }
    }
}
