package com.hadenwatne.fornax.service;

import com.hadenwatne.fornax.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.InputStream;
import java.util.function.Consumer;

public class ResponseService {
    public static void Reply(InteractionHook hook, String reply, boolean mentionUser) {
        hook.sendMessage(reply).mentionRepliedUser(mentionUser).queue();
    }

    public static void Reply(InteractionHook hook, EmbedBuilder response, boolean mentionUser) {
        hook.sendMessageEmbeds(response.build()).mentionRepliedUser(mentionUser).queue();
    }

    public static void Reply(InteractionHook hook, EmbedBuilder response, boolean mentionUser, Consumer<? super Message> onSuccess) {
        hook.sendMessageEmbeds(response.build()).mentionRepliedUser(mentionUser).queue(onSuccess, error -> App.getLogger().LogException(error));
    }

    public static void Reply(InteractionHook hook, EmbedBuilder response, InputStream stream, String fileName, boolean mentionUser) {
        FileUpload fileUpload = FileUpload.fromData(stream, fileName);

        hook.sendFiles(fileUpload).setEmbeds(response.build()).mentionRepliedUser(mentionUser).queue();
    }
}