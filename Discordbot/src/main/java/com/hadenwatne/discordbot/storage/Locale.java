package com.hadenwatne.discordbot.storage;

import java.util.HashMap;

public class Locale {
    public HashMap<Locales, String> messages;
    public final String wildcard = "%WC%";
    public final String linebreak = "%BR%";
    private String localeName;

    public Locale(String name) {
        localeName = name;
        messages = new HashMap<Locales, String>();

        populateDefaultValues();
    }

    public String getLocaleName() {
        return localeName;
    }

    public String getMsg(Locales key) {
        if(messages.containsKey(key)){
            return processBreaks(messages.get(key));
        } else {
            return "Unknown Locale key \""+localeName+"/"+key+"\"";
        }
    }

    public String getMsg(Locales key, String[] replacements) {
        String msg = getMsg(key);

        for(String r : replacements) {
            msg = msg.replaceFirst(wildcard, r);
        }

        return msg;
    }

    private String processBreaks(String msg) {
        msg = msg.replaceAll(linebreak, "\n");

        return msg;
    }

    private void populateDefaultValues() {
        messages.put(Locales.ADD_TRIGGER_SUCCESS, "I will now send a `"+wildcard+"` response when I hear `"+wildcard+"`!");
        messages.put(Locales.BLAME, "I blame "+wildcard);
        messages.put(Locales.CHOOSE, "I choose: "+wildcard+"!");
        messages.put(Locales.FEEDBACK_COOLDOWN, "Please wait a bit before submitting more feedback.");
        messages.put(Locales.FEEDBACK_SENT, ":notepad_spiral: Your feedback has been noted. Thanks!"+linebreak+"You can report again in **5 minutes**.");
        messages.put(Locales.INVALID_TRIGGER_TYPE, ":scream: Invalid trigger type! Your options are: "+wildcard);
        messages.put(Locales.ITEM_ADDED, "Added the things :+1:");
        messages.put(Locales.ITEM_REMOVED, "I've removed \""+wildcard+"\"!");
        messages.put(Locales.RESET_EMOTE_STATS, "We didn't need those anyway ;} #StatsCleared!");
        messages.put(Locales.SENT_PRIVATE_MESSAGE, "PM'd you the deets :punch:");
        messages.put(Locales.TALLY_CURRENT_VALUE, "Current tally for `"+wildcard+"`: `"+wildcard+"`");
        messages.put(Locales.TALLY_LIST, "Here's what I have written down:");
        messages.put(Locales.TALLY_REMOVED, "`"+wildcard+"` hast been removed, sire.");
        messages.put(Locales.TRIGGER_LIST, "I'll respond to these things:");
    }
}
