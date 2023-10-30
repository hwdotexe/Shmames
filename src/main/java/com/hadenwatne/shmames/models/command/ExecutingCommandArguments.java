package com.hadenwatne.shmames.models.command;

import com.hadenwatne.shmames.services.DataService;
import com.hadenwatne.fornax.service.LoggingService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecutingCommandArguments {
    private LinkedHashMap<String, String> arguments;

    public ExecutingCommandArguments(LinkedHashMap<String, String> arguments) {
        this.arguments = arguments;
    }

    public ExecutingCommandArguments() {
        this.arguments = new LinkedHashMap<>();
    }

    public void add(String key, String value) {
        this.arguments.put(key, value);
    }

    public int count() {
        return this.arguments.size();
    }

    public String getAsString() {
        StringBuilder sb = new StringBuilder();

        for(String key : arguments.keySet()) {
            if(sb.length() > 0) {
                sb.append(" ");
            }

            sb.append(arguments.get(key));
        }

        return sb.toString();
    }

    public String getAsString(String key) {
        try {
            return this.arguments.get(key);
        } catch (Exception e) {
            LoggingService.LogException(e);
        }

        return null;
    }

    public boolean getAsBoolean(String key) {
        if(this.arguments.containsKey(key)) {
            try {
                return Boolean.parseBoolean(this.arguments.get(key));
            } catch (Exception e) {
                LoggingService.LogException(e);
            }
        }

        return false;
    }

    public int getAsInteger(String key) {
        if(this.arguments.containsKey(key)) {
            try {
                return Integer.parseInt(this.arguments.get(key));
            } catch (Exception e) {
                LoggingService.LogException(e);
            }
        }

        return -1;
    }

    /**
     * Attempts to convert the argument to a Role. Always returns null for administrator.
     * @param key The parameter key.
     * @param server The server this check is being performed against.
     * @return A Role, if the ID is found, or if the value is "everyone." Otherwise, null.
     */
    public Role getAsRole(String key, Guild server) {
        if(this.arguments.containsKey(key)) {
            try {
                String id = stripID(getAsString(key));

                if (DataService.IsLong(id)) {
                    return server.getRoleById(id);
                } else {
                    if(id.equalsIgnoreCase("everyone") || id.equalsIgnoreCase("@everyone")) {
                        return server.getPublicRole();
                    }
                }
            } catch (Exception e) {
                LoggingService.LogException(e);
            }
        }

        return null;
    }

    public User getAsUser(String key, Guild server) {
        if(this.arguments.containsKey(key)) {
            try {
                String id = stripID(getAsString(key));

                if (DataService.IsLong(id)) {
                    return server.getMemberById(id).getUser();
                }
            } catch (Exception e) {
                LoggingService.LogException(e);
            }
        }

        return null;
    }

    public CustomEmoji getAsEmote(String key, Guild server) {
        if(this.arguments.containsKey(key)) {
            try {
                String id = stripID(getAsString(key));

                if (DataService.IsLong(id)) {
                    return server.getEmojiById(id);
                }
            } catch (Exception e) {
                LoggingService.LogException(e);
            }
        }

        return null;
    }

    public MessageChannel getAsChannel(String key, Guild server) {
        if(this.arguments.containsKey(key)) {
            try {
                String id = stripID(getAsString(key));

                if (DataService.IsLong(id)) {
                    return server.getTextChannelById(id);
                }
            } catch (Exception e) {
                LoggingService.LogException(e);
            }
        }

        return null;
    }

    private String stripID(String discordTag) {
        Matcher m = Pattern.compile("^<[@!&#:]+([a-z0-9_]+:)?(\\d+)>$", Pattern.CASE_INSENSITIVE).matcher(discordTag);

        if(m.find()) {
            return m.group(2);
        }

        return discordTag;
    }
}
