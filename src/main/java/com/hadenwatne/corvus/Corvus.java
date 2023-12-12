package com.hadenwatne.corvus;

import com.hadenwatne.corvus.types.MessageType;
import com.hadenwatne.fornax.App;
import com.hadenwatne.fornax.Bot;
import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.command.types.ExecutionFailReason;
import com.hadenwatne.fornax.command.types.ExecutionStatus;
import com.hadenwatne.fornax.service.types.LogType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Corvus {
    public static CorvusBuilder info(Bot bot) {
        return new CorvusBuilder(bot, MessageType.INFORMATION);
    }

    public static CorvusBuilder error(Bot bot) {
        return new CorvusBuilder(bot, MessageType.ERROR);
    }

    public static CorvusBuilder success(Bot bot) {
        return new CorvusBuilder(bot, MessageType.SUCCESS);
    }

    public static CorvusBuilder privileged(Bot bot) {
        return new CorvusBuilder(bot, MessageType.PRIVILEGED);
    }

    public static CorvusBuilder expired(Bot bot) {
        return new CorvusBuilder(bot, MessageType.EXPIRED);
    }

    public static CorvusBuilder warning(Bot bot) {
        return new CorvusBuilder(bot, MessageType.WARNING);
    }

    public static void reply(Execution execution, CorvusBuilder builder) {
        try {
            if (builder.hasAttachments()) {
                List<FileUpload> uploads = new ArrayList<>();

                for (CorvusAttachment attachment : builder.getAttachments()) {
                    App.getLogger().Log(LogType.DEBUG, attachment.getFileUrl().toString());
                    App.getLogger().Log(LogType.DEBUG, attachment.getFileName());

                    InputStream fileInputStream = attachment.getFileUrl().openStream();

                    uploads.add(FileUpload.fromData(fileInputStream, attachment.getFileName()));
                }

                App.getLogger().Log(LogType.DEBUG, builder.toString());

                execution.getEvent().replyFiles(uploads)
                        .setEmbeds(builder.preBuild().build())
                        .mentionRepliedUser(builder.isMentionAuthor())
                        .setEphemeral(builder.isEphemeral())
                        .setComponents(builder.getLayoutComponents())
                        .queue(result -> result.retrieveOriginal().queue(builder::setMessage), error -> {
                            execution.setStatus(ExecutionStatus.FAILED);
                            execution.setFailureReason(ExecutionFailReason.MISSING_INTERACTION_HOOK);
                            App.getLogger().LogException(error);
                        });
            } else {
                execution.getEvent().replyEmbeds(builder.preBuild().build())
                        .mentionRepliedUser(builder.isMentionAuthor())
                        .setEphemeral(builder.isEphemeral())
                        .setComponents(builder.getLayoutComponents())
                        .queue(result -> result.retrieveOriginal().queue(builder::setMessage), error -> {
                            execution.setStatus(ExecutionStatus.FAILED);
                            execution.setFailureReason(ExecutionFailReason.MISSING_INTERACTION_HOOK);
                            App.getLogger().LogException(error);
                        });
            }
        } catch (IOException exception) {
            App.getLogger().LogException(exception);
        }
    }

    public static RestAction<Message> update(CorvusBuilder builder) {
        return builder.getMessage().editMessageEmbeds(builder.preBuild().build())
                .setComponents(builder.getLayoutComponents());
    }

    public static EmbedBuilder convert(CorvusBuilder corvusBuilder) {
        return corvusBuilder.preBuild();
    }
}