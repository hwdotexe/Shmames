package com.hadenwatne.discordbot.storage;

import java.util.HashMap;

public class Lang {
    public HashMap<Langs, String> messages;
    public final String wildcard = "%WC%";
    public final String linebreak = "%BR%";
    private String langName;

    public Lang(String name) {
        langName = name;
        messages = new HashMap<Langs, String>();

        populateDefaultValues();
    }

    public String getLangName() {
        return langName;
    }

    public String getMsg(Langs key) {
        if(messages.containsKey(key)){
            return processBreaks(messages.get(key));
        } else {
            return "Unknown Lang key \""+ langName +"/"+key+"\"";
        }
    }

    public String getMsg(Langs key, String[] replacements) {
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
        messages.put(Langs.ADD_TRIGGER_SUCCESS, "I will now send a `"+wildcard+"` response when I hear `"+wildcard+"`!");
        messages.put(Langs.BLAME, "I blame "+wildcard);
        messages.put(Langs.CHOOSE, "I choose: "+wildcard+"!");
        messages.put(Langs.FEEDBACK_COOLDOWN, "Please wait a bit before submitting more feedback.");
        messages.put(Langs.FEEDBACK_SENT, ":notepad_spiral: Your feedback has been noted. Thanks!"+linebreak+"You can report again in **5 minutes**.");
        messages.put(Langs.INVALID_TRIGGER_TYPE, ":scream: Invalid trigger type! Your options are: "+wildcard);
        messages.put(Langs.ITEM_ADDED, "Added the things :+1:");
        messages.put(Langs.ITEM_REMOVED, "I've removed \""+wildcard+"\"!");
        messages.put(Langs.RESET_EMOTE_STATS, "We didn't need those anyway ;} #StatsCleared!");
        messages.put(Langs.SENT_PRIVATE_MESSAGE, "PM'd you the deets :punch:");
        messages.put(Langs.TALLY_CURRENT_VALUE, "Current tally for `"+wildcard+"`: `"+wildcard+"`");
        messages.put(Langs.TALLY_LIST, "Here's what I have written down:");
        messages.put(Langs.TALLY_REMOVED, "`"+wildcard+"` hast been removed, sire.");
        messages.put(Langs.TRIGGER_LIST, "I'll respond to these things:");
    }
}
