package com.hadenwatne.discordbot.storage;

import com.hadenwatne.discordbot.Shmames;

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
            if(!langName.equalsIgnoreCase("default")) {
                return Shmames.getDefaultLang().getMsg(key);
            } else {
                return "Unknown Lang key \""+key+"\"\n> **You should report this error to the developer!**";
            }
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
        // TODO Add another map for Errors, use Error name instead of hard-coding type.

        messages.put(Langs.ADD_TRIGGER_SUCCESS,         "I will now send a `"+wildcard+"` response when I hear `"+wildcard+"`!");
        messages.put(Langs.BLAME,                       "I blame "+wildcard);
        messages.put(Langs.CHOOSE,                      "I choose: "+wildcard+"!");
        messages.put(Langs.EMOTE_STATS_TITLE,           "The Emoji Abacus doth say:");
        messages.put(Langs.FAMILY_CREATED,              "The Family was created! Now let's go add other servers!");
        messages.put(Langs.FAMILY_JOIN_CODE,            "**Join Code for " + wildcard + "**"+linebreak+"`" + wildcard + "`"+linebreak+"_Use this one-time code to join a server to the Family._");
        messages.put(Langs.FAMILY_JOIN_CODE_INVALIDATED,"[The Join Code has been invalidated for security purposes]");
        messages.put(Langs.FAMILY_JOINED,               "Added **" + wildcard + "** to the **" + wildcard + "** Family!");
        messages.put(Langs.FAMILY_REMOVED_SERVER,       "Removed **" + wildcard + "** from the **" + wildcard + "** Family!");
        messages.put(Langs.FAMILY_SERVER_LIST,          "The \""+wildcard+"\" Family contains the following servers:");
        messages.put(Langs.FEEDBACK_COOLDOWN,           "Please wait a bit before submitting more feedback.");
        messages.put(Langs.FEEDBACK_SENT,               ":notepad_spiral: Your feedback has been noted. Thanks!"+linebreak+"You can report again in **5 minutes**.");
        messages.put(Langs.FORUM_WEAPON_ADDED_ALIAS,    "The alias was added!");
        messages.put(Langs.FORUM_WEAPON_CREATED,        "Created a new Forum Weapon: **"+wildcard+"**");
        messages.put(Langs.FORUM_WEAPON_DESTROYED,      "Weapon destroyed.");
        messages.put(Langs.FORUM_WEAPON_DUPLICATE,      ":warning: Found an existing Forum Weapon with that link: **"+wildcard+"**");
        messages.put(Langs.FORUM_WEAPON_UPDATED,        "The weapon was updated with a new link!");
        messages.put(Langs.GENERIC_SUCCESS,             "Success!");
        messages.put(Langs.HANGMAN_FOOTER_GUESSED,      "Already guessed:");
        messages.put(Langs.HANGMAN_TITLE,               "Let's play Hangman!");
        messages.put(Langs.INVALID_TRIGGER_TYPE,        ":scream: Invalid trigger type! Your options are: "+wildcard);
        messages.put(Langs.ITEM_ADDED,                  "Added the things :+1:");
        messages.put(Langs.ITEM_REMOVED,                "I've removed \""+wildcard+"\"!");
        messages.put(Langs.POLL_TITLE,                  "== POLL ==");
        messages.put(Langs.POLL_TITLE_RESULTS,          "== POLL (Results) ==");
        messages.put(Langs.RESET_EMOTE_STATS,           "We didn't need those anyway ;} #StatsCleared!");
        messages.put(Langs.SENT_PRIVATE_MESSAGE,        "PM'd you the deets :punch:");
        messages.put(Langs.SERVER_FAMILY_LIST,          "This server has joined the following Families:");
        messages.put(Langs.SETTING_LIST_TITLE,          "Available settings:");
        messages.put(Langs.SETTING_UPDATED_SUCCESS,     "Setting was updated successfully!");
        messages.put(Langs.TALLY_CURRENT_VALUE,         "Current tally for `"+wildcard+"`: `"+wildcard+"`");
        messages.put(Langs.TALLY_LIST,                  "Here's what I have written down:");
        messages.put(Langs.TALLY_REMOVED,               "`"+wildcard+"` hast been removed, sire.");
        messages.put(Langs.TIMER_STARTED,               "Started a new :alarm_clock: for "+wildcard);
        messages.put(Langs.TRIGGER_LIST,                "I'll respond to these things:");
    }
}
