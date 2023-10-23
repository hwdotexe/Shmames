package com.hadenwatne.botcore.command;

import com.hadenwatne.botcore.service.ResponseService;
import com.hadenwatne.botcore.type.ExecutionFailReason;
import com.hadenwatne.botcore.type.ExecutionStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;
import java.util.function.Consumer;

public class Execution {
    // Status [RUNNING, COMPLETED, FAILED]
    // Fail reason
    // Event
    // Command
    // Server
    // User

    private Command _command;
    private String _subCommandGroup;
    private String _subCommand;
    private List<OptionMapping> _arguments;
    private SlashCommandInteractionEvent _event;
    private InteractionHook _hook;
    private ExecutionStatus _status;
    private ExecutionFailReason _failureReason;

    public Execution(Command command, SlashCommandInteractionEvent event) {
        _command = command;
        _event = event;
        _hook = event.getHook();
        _status = ExecutionStatus.STARTED;
        _failureReason = ExecutionFailReason.NONE;
        _arguments = event.getOptions();
    }

    public List<OptionMapping> getArguments() {
        return _arguments;
    }

    public ExecutionStatus getStatus() {
        return _status;
    }

    public void setStatus(ExecutionStatus status) {
        this._status = status;
    }

    public void setFailureReason(ExecutionFailReason failureReason) {
        this._failureReason = failureReason;
    }

    public ExecutionFailReason getFailureReason() {
        return _failureReason;
    }

    public Command getCommand() {
        return _command;
    }

    public User getUser() {
        return this._event.getUser();
    }

    public Member getMember() {
        if (this._event.isFromGuild()) {
            return this._event.getMember();
        } else {
            return null;
        }
    }

    public boolean isFromServer() {
        return this._event.isFromGuild();
    }

    public Guild getServer() {
        if (this._event.isFromGuild()) {
            return this._event.getGuild();
        } else {
            return null;
        }
    }

    public MessageChannelUnion getChannel() {
        return this._event.getChannel();
    }

    public void setEphemeral(boolean ephemeral) {
        this._hook.setEphemeral(ephemeral);
    }

    // TODO: Reply with modal and callback? Call method in Command that gets overridden?

    public void reply(String reply) {
        ResponseService.Reply(this._hook, reply, false);
    }

    public void reply(EmbedBuilder reply) {
        ResponseService.Reply(this._hook, reply, false);
    }

    public void reply(EmbedBuilder reply, boolean mentionUser, Consumer<? super Message> onSuccess) {
        ResponseService.Reply(this._hook, reply, mentionUser, onSuccess);
    }
}
