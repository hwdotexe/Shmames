package com.hadenwatne.shmames.models.command;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

public class ShmamesCommandArguments {
    private HashMap<String, Object> arguments;

    public ShmamesCommandArguments(HashMap<String, Object> arguments) {
        this.arguments = arguments;
    }

    public int count() {
        return this.arguments.size();
    }

    public String getAsString(String key) {
        return (String) this.arguments.get(key);
    }

    public boolean getAsBoolean(String key) {
        return Boolean.parseBoolean(getAsString(key));
    }

    public int getAsInteger(String key) {
        return Integer.parseInt(getAsString(key));
    }

    public Role getAsRole(String key) {
        return (Role) this.arguments.get(key);
    }

    public User getAsUser(String key) {
        return (User) this.arguments.get(key);
    }

    public Emote getAsEmote(String key) {
        return (Emote) this.arguments.get(key);
    }

    public MessageChannel getAsChannel(String key) {
        return (MessageChannel) this.arguments.get(key);
    }

}
