package com.hadenwatne.discordbot.storage;

import java.util.HashMap;

public class Locale {
    public HashMap<String, String> messages;
    public final String wildcard = "%REP%";
    private String localeName;

    public Locale(String name) {
        localeName = name;
        messages = new HashMap<String, String>();

        populateDefaultValues();
    }

    public String getLocaleName() {
        return localeName;
    }

    public String getMsg(String key) {
        if(messages.containsKey(key.toLowerCase())){
            return messages.get(key.toLowerCase());
        } else {
            return "Unknown key \""+key+"\"";
        }
    }

    public String getMsg(String key, String replace) {
        return getMsg(key).replaceFirst(wildcard, replace);
    }

    private void populateDefaultValues() {

    }
}
