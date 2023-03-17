package com.hadenwatne.shmames.services;

import com.hadenwatne.shmames.App;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;

import java.util.ArrayList;
import java.util.List;

public class PermissionsService {
    public static void AuditServerPermissions(JDA jda) {
        List<Permission> permissions = new ArrayList<>();

        permissions.add(Permission.MESSAGE_SEND);
        permissions.add(Permission.MESSAGE_EMBED_LINKS);
        permissions.add(Permission.MESSAGE_HISTORY);
        permissions.add(Permission.VIEW_CHANNEL);
        permissions.add(Permission.MESSAGE_SEND_IN_THREADS);
        permissions.add(Permission.VOICE_SPEAK);
        permissions.add(Permission.VOICE_CONNECT);
        permissions.add(Permission.MANAGE_ROLES);
        permissions.add(Permission.MESSAGE_EXT_EMOJI);
        permissions.add(Permission.MESSAGE_ADD_REACTION);

        for (Guild server : jda.getGuilds()) {
            StringBuilder noPerms = new StringBuilder();

            for (Permission p : permissions) {
                if (!server.getSelfMember().hasPermission(p)) {
                    if (noPerms.length() > 0) {
                        noPerms.append(System.lineSeparator());
                    }

                    noPerms.append(p.getName());
                }
            }

            if (noPerms.length() > 0) {
                noPerms.insert(0, ":warning: " + App.Shmames.getBotName() + " requires the following permissions in order to function properly: " + System.lineSeparator());
                noPerms.append(System.lineSeparator());
                noPerms.append("Please enable these permissions in your server's role settings for the best experience.");

                try {
                    if(server.getDefaultChannel().getType() == ChannelType.TEXT && server.getDefaultChannel().asTextChannel().canTalk()) {
                        MessageService.SendSimpleMessage(server.getDefaultChannel().asTextChannel(), noPerms.toString());
                    } else {
                        MessageService.SendSimpleMessage(server.getSystemChannel(), noPerms.toString());
                    }
                } catch (Exception e) {
                    LoggingService.LogException(e);
                }
            }
        }
    }
}
