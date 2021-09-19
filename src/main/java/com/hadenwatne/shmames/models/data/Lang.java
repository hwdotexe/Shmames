package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Lang {
    public LinkedHashMap<Langs, String> messages;
    public LinkedHashMap<Errors, String> errors;
    public final String wildcard = "%WC%";
    public final String linebreak = "%BR%";
    private String langName;

    public Lang(String name) {
        langName = name;
        messages = new LinkedHashMap<Langs, String>();
        errors = new LinkedHashMap<Errors, String>();

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
            // $ characters need to be dynamically escaped, otherwise Java will think it's an improper regex anchor.
            msg = msg.replaceFirst(wildcard, r.replaceAll("\\$", "__DOLLARSIGN__"));
        }

        msg = msg.replaceAll("__DOLLARSIGN__", "\\$");

        return msg;
    }

    public String getError(Errors key, boolean showError) {
        String s = showError ? ("\n`// " + key.toString() + "`") : "";

        if (errors.containsKey(key)) {
            return processBreaks(errors.get(key)) + s;
        } else {
            if (!langName.equalsIgnoreCase("default")) {
                return Shmames.getDefaultLang().getError(key, showError) + s;
            } else {
                return "Unknown Lang key \"" + key + "\"\n> **You should report this error to the developer!**";
            }
        }
    }

    public String getError(Errors key, boolean showError, String[] replacements) {
        String msg = getError(key, showError);

        for(String r : replacements) {
            msg = msg.replaceFirst(wildcard, r);
        }

        return msg;
    }

    public String wrongUsage(String usage) {
        return getError(Errors.WRONG_USAGE, true) + "\n> " + getMsg(Langs.COMMAND_USAGE, new String[]{usage});
    }

    private String processBreaks(String msg) {
        msg = msg.replaceAll(linebreak, "\n");

        return msg;
    }

    private void populateDefaultValues() {
        errors.put(Errors.ALREADY_EXISTS,                       "I think you've already done that!");
        errors.put(Errors.BOT_ERROR,                            "I sense a plot to destroy me.");
        errors.put(Errors.CANNOT_DELETE,                        "Sorry, I can't let you delete that. It's very precious to me.");
        errors.put(Errors.CHANNEL_NOT_FOUND,                    "I can't find the correct channel for that.");
        errors.put(Errors.COMMAND_NOT_FOUND,                    "That command hasn't been invented yet!");
        errors.put(Errors.FAMILY_ALREADY_EXISTS,                "You already own a family with that name! Please choose a different name.");
        errors.put(Errors.FAMILY_ALREADY_JOINED,                "This server already belongs to that family!");
        errors.put(Errors.FAMILY_INVALID_DETAIL,                "Invalid Family name or Join Code!");
        errors.put(Errors.FAMILY_NOT_JOINED,                    "That server has not joined that Family!");
        errors.put(Errors.FAMILY_MEMBER_MAXIMUM_REACHED,        "That family has reached the maximum number of servers!");
        errors.put(Errors.FAMILY_MAXIMUM_REACHED,               "You can only join up to 3 families!");
        errors.put(Errors.FAMILY_SERVER_LIST_EMPTY,             "This Family does not contain any servers.");
        errors.put(Errors.FORUM_WEAPON_MAXIMUM_REACHED,         "Sorry! I can only keep up to 100 weapons. Please remove some existing weapons before creating more.");
        errors.put(Errors.FORUM_WEAPON_OWNED_OTHER,             "That weapon is owned by a different server!");
        errors.put(Errors.GUILD_REQUIRED,                       "That command must be run on a server.");
        errors.put(Errors.HANGMAN_ALREADY_GUESSED,              "You've already guessed that letter!");
        errors.put(Errors.HANGMAN_NOT_STARTED,                  "There isn't a Hangman game running! Try starting one.");
        errors.put(Errors.HEY_THERE,                            "Hey there! Try using `"+wildcard+" help`!");
        errors.put(Errors.INCOMPLETE,                           "I'm gonna need a few more details.");
        errors.put(Errors.INCORRECT_ITEM_COUNT,                 "You've supplied an incorrect number of thingz!");
        errors.put(Errors.ITEMS_NOT_FOUND,                      "There weren't any results.");
        errors.put(Errors.MUSIC_NOT_IN_CHANNEL,                 "Please join a voice channel and run this command again.");
        errors.put(Errors.MUSIC_PLAYLIST_ALREADY_EXISTS,        "A Playlist with that name already exists on this server!");
        errors.put(Errors.MUSIC_PLAYLIST_DOESNT_EXIST,          "That Playlist doesn't exist!");
        errors.put(Errors.MUSIC_PLAYLIST_EMPTY,                 "There are no tracks in that Playlist.");
        errors.put(Errors.MUSIC_PLAYLIST_LIST_EMPTY,            "There aren't any Playlists yet!");
        errors.put(Errors.MUSIC_PLAYLIST_NAME_INVALID,          "Playlist names must be alphanumeric!");
        errors.put(Errors.MUSIC_PLAYLIST_NAME_MISSING,          "Please enter a name for the new Playlist.");
        errors.put(Errors.MUSIC_PLAYLIST_PAGE_EMPTY,            "There are no tracks in that Playlist on this page.");
        errors.put(Errors.MUSIC_PLAYLIST_TRACK_MAXIMUM_REACHED, "Playlists currently support a max of 50 tracks.");
        errors.put(Errors.MUSIC_QUEUE_EMPTY,                    "There are no tracks in the Queue.");
        errors.put(Errors.MUSIC_QUEUE_PAGE_EMPTY,               "There are no tracks in the Queue on this page.");
        errors.put(Errors.MUSIC_WRONG_INPUT,                    "Please enter a media URL or playlist name!");
        errors.put(Errors.NO_PERMISSION_BOT,                    "I ran into some trouble with the law...");
        errors.put(Errors.NO_PERMISSION_USER,                   "I'm afraid I can't let you do that.");
        errors.put(Errors.NOT_FOUND,                            "That thing you said... I'm not sure what it is.");
        errors.put(Errors.RESERVED_WORD,                        "Sorry, you can't use that totally awesome name!");
        errors.put(Errors.SERVER_FAMILY_LIST_EMPTY,             "This server does not belong to a Family.");
        errors.put(Errors.SETTING_NOT_FOUND,                    "I couldn't find that setting.");
        errors.put(Errors.TIME_VALUE_INCORRECT,                 "The amount of time provided is invalid!");
        errors.put(Errors.TRACK_NOT_PLAYING,                    "There isn't a track playing right now.");
        errors.put(Errors.WRONG_USAGE,                          "I don't think that's how you do it.");

        messages.put(Langs.ADD_TRIGGER_SUCCESS,                 "I will now send a `"+wildcard+"` response when I hear `"+wildcard+"`!");
        messages.put(Langs.BLAME,                               "I blame "+wildcard);
        messages.put(Langs.CHOOSE,                              "I choose: "+wildcard+"!");
        messages.put(Langs.COMMAND_USAGE,                       "Give this a try: `"+wildcard+"`");
        messages.put(Langs.EMOTE_STATS_TITLE,                   "The Emoji Abacus doth say:");
        messages.put(Langs.FAMILY_CREATED,                      "The Family was created! Now let's go add other servers!");
        messages.put(Langs.FAMILY_JOIN_CODE,                    "**Join Code for " + wildcard + "**"+linebreak+"`" + wildcard + "`"+linebreak+"_Use this one-time code to join a server to the Family._");
        messages.put(Langs.FAMILY_JOIN_CODE_INVALIDATED,        "[The Join Code has been invalidated for security purposes]");
        messages.put(Langs.FAMILY_JOINED,                       "Added **" + wildcard + "** to the **" + wildcard + "** Family!");
        messages.put(Langs.FAMILY_REMOVED_SERVER,               "Removed **" + wildcard + "** from the **" + wildcard + "** Family!");
        messages.put(Langs.FAMILY_SERVER_LIST,                  "The \""+wildcard+"\" Family contains the following servers:");
        messages.put(Langs.FEEDBACK_COOLDOWN,                   "Please wait a bit before submitting more feedback.");
        messages.put(Langs.FEEDBACK_SENT,                       ":notepad_spiral: Your feedback has been noted. Thanks!"+linebreak+"You can report again in **5 minutes**.");
        messages.put(Langs.FORUM_WEAPON_ADDED_ALIAS,            "The alias was added!");
        messages.put(Langs.FORUM_WEAPON_CREATED,                "Created a new Forum Weapon: **"+wildcard+"**");
        messages.put(Langs.FORUM_WEAPON_DESTROYED,              "Weapon destroyed.");
        messages.put(Langs.FORUM_WEAPON_DUPLICATE,              ":warning: Found an existing Forum Weapon with that link: **"+wildcard+"**");
        messages.put(Langs.FORUM_WEAPON_UPDATED,                "The weapon was updated with a new link!");
        messages.put(Langs.FORUM_WEAPONS_PRUNED,                "Pruned **"+wildcard+"** unused Forum Weapons!");
        messages.put(Langs.GENERIC_SUCCESS,                     "Success!");
        messages.put(Langs.HANGMAN_FOOTER_GUESSED,              "Already guessed:");
        messages.put(Langs.HANGMAN_TITLE,                       "Let's play Hangman!");
        messages.put(Langs.INVALID_TRIGGER_TYPE,                ":scream: Invalid trigger type! Your options are: "+wildcard);
        messages.put(Langs.ITEM_ADDED,                          "Added the things :+1:");
        messages.put(Langs.ITEM_REMOVED,                        "I've removed \""+wildcard+"\"!");
        messages.put(Langs.LIST_CREATED,                        "The List was created!");
        messages.put(Langs.LIST_DELETED,                        "Deleted the `"+wildcard+"` List!");
        messages.put(Langs.MUSIC_ADDED_TO_QUEUE,                "Added to queue!");
        messages.put(Langs.MUSIC_LOOPING_TOGGLED,               "Music looping is now **"+wildcard+"**");
        messages.put(Langs.MUSIC_LOOPING_QUEUE_TOGGLED,         "Music Queue looping is now **"+wildcard+"**");
        messages.put(Langs.MUSIC_PLAYING,                       "Playing!");
        messages.put(Langs.MUSIC_PLAYING_PLAYLIST,              "Playing the `"+wildcard+"` Playlist!");
        messages.put(Langs.MUSIC_PLAYLIST_CREATED,              "Created a new Playlist `"+wildcard+"`!");
        messages.put(Langs.MUSIC_PLAYLIST_CONVERTED,            "Converted the queue into a new Playlist `"+wildcard+"` with `"+wildcard+"` tracks!");
        messages.put(Langs.MUSIC_PLAYLIST_DELETED,              "Playlist deleted!");
        messages.put(Langs.MUSIC_PLAYLIST_TRACK_ADDED,          "Track added!");
        messages.put(Langs.MUSIC_PLAYLIST_TRACK_REMOVED,        "Track removed!");
        messages.put(Langs.MUSIC_QUEUE_CLEARED,                 "Cleared the music queue!");
        messages.put(Langs.MUSIC_QUEUE_SHUFFLED,                "Shuffled the music queue!");
        messages.put(Langs.MUSIC_QUEUE_REVERSED,                "Reversed the music queue!");
        messages.put(Langs.MUSIC_QUEUED_PLAYLIST,               "Queued the `"+wildcard+"` Playlist!");
        messages.put(Langs.POLL_TITLE,                          "== POLL ==");
        messages.put(Langs.POLL_TITLE_RESULTS,                  "== POLL (Results) ==");
        messages.put(Langs.RESET_EMOTE_STATS,                   "We didn't need those anyway ;} #StatsCleared!");
        messages.put(Langs.SENT_PRIVATE_MESSAGE,                "PM'd you the deets :punch:");
        messages.put(Langs.SERVER_FAMILY_LIST,                  "This server has joined the following Families:");
        messages.put(Langs.SETTING_LIST_TITLE,                  "Available settings:");
        messages.put(Langs.SETTING_UPDATED_SUCCESS,             "Setting was updated successfully!");
        messages.put(Langs.TALLY_CURRENT_VALUE,                 "Current tally for `"+wildcard+"`: `"+wildcard+"`");
        messages.put(Langs.TALLY_LIST,                          "Here's what I have written down:");
        messages.put(Langs.TALLY_REMOVED,                       "`"+wildcard+"` hast been removed, sire.");
        messages.put(Langs.TIMER_STARTED,                       "Started a new :alarm_clock: for "+wildcard);
        messages.put(Langs.TRIGGER_LIST,                        "I'll respond to these things:");
    }
}
