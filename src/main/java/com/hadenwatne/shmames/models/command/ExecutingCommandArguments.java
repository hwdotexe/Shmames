package com.hadenwatne.shmames.models.command;

import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.services.DataService;
import net.dv8tion.jda.api.entities.*;

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

    public Role getAsRole(String key, Guild server) {
        if(this.arguments.containsKey(key)) {
            try {
                String id = stripID(getAsString(key));

                if (DataService.IsLong(id)) {
                    return server.getRoleById(id);
                } else {
                    return server.getRolesByName(id, true).get(0);
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
                } else {
                    return server.getMembersByName(id, true).get(0).getUser();
                }
            } catch (Exception e) {
                LoggingService.LogException(e);
            }
        }

        return null;
    }

    public Emote getAsEmote(String key, Guild server) {
        if(this.arguments.containsKey(key)) {
            try {
                String id = stripID(getAsString(key));

                if (DataService.IsLong(id)) {
                    return server.getEmoteById(id);
                } else {
                    return server.getEmotesByName(id, true).get(0);
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
                } else {
                    return server.getTextChannelsByName(id, true).get(0);
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
