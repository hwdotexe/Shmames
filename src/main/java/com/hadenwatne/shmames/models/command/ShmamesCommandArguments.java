package com.hadenwatne.shmames.models.command;

import com.hadenwatne.shmames.ShmamesLogger;
import com.hadenwatne.shmames.Utils;
import net.dv8tion.jda.api.entities.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShmamesCommandArguments {
    private LinkedHashMap<String, Object> arguments;

    public ShmamesCommandArguments(LinkedHashMap<String, Object> arguments) {
        this.arguments = arguments;
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
        return (String) this.arguments.get(key);
    }

    public boolean getAsBoolean(String key) {
        if(this.arguments.containsKey(key)) {
            try {
                return (boolean) this.arguments.get(key);
            } catch (Exception e) {
                ShmamesLogger.logException(e);
            }
        }

        return false;
    }

    public int getAsInteger(String key) {
        if(this.arguments.containsKey(key)) {
            try {
                return (Integer) this.arguments.get(key);
            } catch (Exception e) {
                ShmamesLogger.logException(e);
            }
        }

        return -1;
    }

    public Role getAsRole(String key, Guild server) {
        if(this.arguments.containsKey(key)) {
            try {
                String id = stripID(getAsString(key));

                if (Utils.isLong(id)) {
                    return server.getRoleById(id);
                } else {
                    return server.getRolesByName(id, true).get(0);
                }
            } catch (Exception e) {
                ShmamesLogger.logException(e);
            }
        }

        return null;
    }

    public User getAsUser(String key, Guild server) {
        if(this.arguments.containsKey(key)) {
            try {
                String id = stripID(getAsString(key));

                if (Utils.isLong(id)) {
                    return server.getMemberById(id).getUser();
                } else {
                    return server.getMembersByName(id, true).get(0).getUser();
                }
            } catch (Exception e) {
                ShmamesLogger.logException(e);
            }
        }

        return null;
    }

    public Emote getAsEmote(String key, Guild server) {
        if(this.arguments.containsKey(key)) {
            try {
                String id = stripID(getAsString(key));

                if (Utils.isLong(id)) {
                    return server.getEmoteById(id);
                } else {
                    return server.getEmotesByName(id, true).get(0);
                }
            } catch (Exception e) {
                ShmamesLogger.logException(e);
            }
        }

        return null;
    }

    public MessageChannel getAsChannel(String key, Guild server) {
        if(this.arguments.containsKey(key)) {
            try {
                String id = stripID(getAsString(key));

                if (Utils.isLong(id)) {
                    return server.getTextChannelById(id);
                } else {
                    return server.getTextChannelsByName(id, true).get(0);
                }
            } catch (Exception e) {
                ShmamesLogger.logException(e);
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
