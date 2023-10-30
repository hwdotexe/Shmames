package com.hadenwatne.corvus;

import com.hadenwatne.corvus.types.MessageType;
import com.hadenwatne.fornax.App;
import com.hadenwatne.fornax.Bot;
import com.hadenwatne.fornax.command.Execution;
import net.dv8tion.jda.api.interactions.InteractionHook;
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
            InteractionHook hook = execution.getHook();

            if (builder.hasAttachments()) {
                List<FileUpload> uploads = new ArrayList<>();

                for (CorvusAttachment attachment : builder.getAttachments()) {
                    InputStream fileInputStream = attachment.getFileUrl().openStream();

                    uploads.add(FileUpload.fromData(fileInputStream, attachment.getFileName()));
                }

                hook.sendFiles(uploads)
                        .setEmbeds(builder.preBuild().build())
                        .mentionRepliedUser(builder.isMentionAuthor())
                        .setEphemeral(builder.isEphemeral())
                        .setComponents(builder.getLayoutComponents())
                        .queue();
            } else {
                hook.sendMessageEmbeds(builder.preBuild().build())
                        .mentionRepliedUser(builder.isMentionAuthor())
                        .setEphemeral(builder.isEphemeral())
                        .setComponents(builder.getLayoutComponents())
                        .queue();
            }
        } catch (IOException exception) {
            App.getLogger().LogException(exception);
        }
    }
}