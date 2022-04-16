package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.App;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.services.LoggingService;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.enums.Langs;
import com.hadenwatne.shmames.enums.LogType;
import com.hadenwatne.shmames.services.RandomService;

import java.util.LinkedHashMap;

public class Lang {
    public LinkedHashMap<Langs, String[]> messages;
    public LinkedHashMap<Errors, String[]> errors;
    public final String wildcard = "%WC%";
    public final String linebreak = "%BR%";
    private String langName;

    public Lang(String name) {
        langName = name;
        messages = new LinkedHashMap<Langs, String[]>();
        errors = new LinkedHashMap<Errors, String[]>();

        populateDefaultValues();
    }

    public String getLangName() {
        return langName;
    }

    public String getMsg(Langs key) {
        if (messages.containsKey(key)) {
            String[] messageArray = messages.get(key);

            if (messageArray.length > 1) {
                return processBreaks(messageArray[RandomService.GetRandom(messageArray.length)]);
            }

            return processBreaks(messageArray[0]);
        } else {
            if (!langName.equalsIgnoreCase("default")) {
                return App.Shmames.getLanguageService().getDefaultLang().getMsg(key);
            } else {
                return "Unknown Lang key \"" + key + "\"\n> **You should report this error to the developer!**";
            }
        }
    }

    public String getMsg(Langs key, String[] replacements) {
        String msg = getMsg(key);

        for (String r : replacements) {
            // $ characters need to be dynamically escaped, otherwise Java will think it's an improper regex anchor.
            msg = msg.replaceFirst(wildcard, r.replaceAll("\\$", "__DOLLARSIGN__"));
        }

        msg = msg.replaceAll("__DOLLARSIGN__", "\\$");

        return msg;
    }

    public String getError(Errors key) {
        if (errors.containsKey(key)) {
            String[] messageArray = errors.get(key);

            if (messageArray.length > 1) {
                return processBreaks(messageArray[RandomService.GetRandom(messageArray.length)]);
            }

            return processBreaks(errors.get(key)[0]);
        } else {
            if (!langName.equalsIgnoreCase("default")) {
                return App.Shmames.getLanguageService().getDefaultLang().getError(key);
            } else {
                LoggingService.Log(LogType.ERROR, "An unknown Lang key was used: " + key);
                return "Unknown Lang key \"" + key + "\"\n> **You should report this error to the developer!**";
            }
        }
    }

    public String getError(Errors key, String[] replacements) {
        String msg = getError(key);

        for (String r : replacements) {
            msg = msg.replaceFirst(wildcard, r);
        }

        return msg;
    }

    private String processBreaks(String msg) {
        msg = msg.replaceAll(linebreak, "\n");

        return msg;
    }

    private void populateDefaultValues() {
        errors.put(Errors.ALREADY_EXISTS, new String[]{"That item already exists!"});
        errors.put(Errors.BOT_ERROR, new String[]{"There was an internal error, and your request did not complete."});
        errors.put(Errors.CANNOT_DELETE, new String[]{"Sorry, I can't let you delete that. It's very precious to me."});
        errors.put(Errors.CHANNEL_NOT_FOUND, new String[]{"I can't find the correct channel for that."});
        errors.put(Errors.COMMAND_NOT_FOUND, new String[]{"That command hasn't been invented yet!"});
        errors.put(Errors.FAMILY_ALREADY_EXISTS, new String[]{"You already own a family with that name! Please choose a different name."});
        errors.put(Errors.FAMILY_ALREADY_JOINED, new String[]{"This server already belongs to that family!"});
        errors.put(Errors.FAMILY_INVALID_DETAIL, new String[]{"Invalid Family name or Join Code!"});
        errors.put(Errors.FAMILY_NOT_JOINED, new String[]{"That server has not joined that Family!"});
        errors.put(Errors.FAMILY_MEMBER_MAXIMUM_REACHED, new String[]{"That family has reached the maximum number of servers!"});
        errors.put(Errors.FAMILY_MAXIMUM_REACHED, new String[]{"You can only join up to 3 families!"});
        errors.put(Errors.FAMILY_SERVER_LIST_EMPTY, new String[]{"This Family does not contain any servers."});
        errors.put(Errors.FORUM_WEAPON_MAXIMUM_REACHED, new String[]{"Sorry! I can only keep up to 100 weapons. Please remove some existing weapons before creating more."});
        errors.put(Errors.FORUM_WEAPON_OWNED_OTHER, new String[]{"That weapon is owned by a different server!"});
        errors.put(Errors.GUILD_REQUIRED, new String[]{"That command must be run on a server."});
        errors.put(Errors.HANGMAN_ALREADY_GUESSED, new String[]{"You've already guessed that letter!"});
        errors.put(Errors.HANGMAN_NOT_STARTED, new String[]{"There isn't a Hangman game running! Try starting one."});
        errors.put(Errors.HEY_THERE, new String[]{"Hey there! Try using `" + wildcard + " help`!"});
        errors.put(Errors.INCOMPLETE, new String[]{"I'm gonna need a few more details."});
        errors.put(Errors.INCORRECT_ITEM_COUNT, new String[]{"Incorrect number of arguments provided!"});
        errors.put(Errors.ITEMS_NOT_FOUND, new String[]{"There weren't any results."});
        errors.put(Errors.MUSIC_NOT_IN_CHANNEL, new String[]{"Please join a voice channel and run this command again."});
        errors.put(Errors.MUSIC_PLAYLIST_ALREADY_EXISTS, new String[]{"A Playlist with that name already exists on this server!"});
        errors.put(Errors.MUSIC_PLAYLIST_DOESNT_EXIST, new String[]{"That Playlist doesn't exist!"});
        errors.put(Errors.MUSIC_PLAYLIST_EMPTY, new String[]{"There are no tracks in that Playlist."});
        errors.put(Errors.MUSIC_PLAYLIST_LIST_EMPTY, new String[]{"There aren't any Playlists yet!"});
        errors.put(Errors.MUSIC_PLAYLIST_NAME_INVALID, new String[]{"Playlist names must be alphanumeric!"});
        errors.put(Errors.MUSIC_PLAYLIST_NAME_MISSING, new String[]{"Please enter a name for the new Playlist."});
        errors.put(Errors.MUSIC_PLAYLIST_PAGE_EMPTY, new String[]{"There are no tracks in that Playlist on this page."});
        errors.put(Errors.MUSIC_PLAYLIST_TRACK_MAXIMUM_REACHED, new String[]{"Playlists currently support a max of 50 tracks."});
        errors.put(Errors.MUSIC_QUEUE_EMPTY, new String[]{"There are no tracks in the Queue."});
        errors.put(Errors.MUSIC_QUEUE_PAGE_EMPTY, new String[]{"There are no tracks in the Queue on this page."});
        errors.put(Errors.MUSIC_WRONG_INPUT, new String[]{"Please enter a media URL or playlist name!"});
        errors.put(Errors.NO_PERMISSION_BOT, new String[]{"I don't have permission to do that on this server."});
        errors.put(Errors.NO_PERMISSION_USER, new String[]{"I'm afraid I can't let you do that."});
        errors.put(Errors.NOT_FOUND, new String[]{"There were no results."});
        errors.put(Errors.PAGE_NOT_FOUND, new String[]{"The page you requested is empty or does not exist."});
        errors.put(Errors.RESERVED_WORD, new String[]{"It looks like you tried to use a reserved word. Try a different one!"});
        errors.put(Errors.SERVER_FAMILY_LIST_EMPTY, new String[]{"This server does not belong to a Family."});
        errors.put(Errors.SETTING_NOT_FOUND, new String[]{"I couldn't find that setting."});
        errors.put(Errors.TIME_VALUE_INCORRECT, new String[]{"The amount of time provided is invalid!"});
        errors.put(Errors.TRACK_NOT_PLAYING, new String[]{"There isn't a track playing right now."});
        errors.put(Errors.WRONG_USAGE, new String[]{"The command syntax you used is incorrect."});

        messages.put(Langs.ADD_TRIGGER_SUCCESS, new String[]{"I will now send a `" + wildcard + "` response when I hear `" + wildcard + "`!"});
        messages.put(Langs.BLAME, new String[]{"I blame " + wildcard});
        messages.put(Langs.BLAME_OPTIONS, new String[]{"Obama", "Trump", "Blizzard", "China", "EA", "4Chan", "your mom", "the economy", "Big Pharma", "India", "Nigeria", "Mexico", "Chemtrails", "GMOs", "vaccines", "#VapeLife", "weebs", "essential oils", "Karen", "Epic Games", "video games", "hip hop", "Fortnite", "Source Filmmaker", "Discord", "Coronavirus", "Apple", "Google", "Starbucks", "NASA", "Keanu Reeves", "Oscar the Grouch", "Ohio"});
        messages.put(Langs.CHOOSE, new String[]{"I choose: " + wildcard + "!"});
        messages.put(Langs.COMMAND_USAGE, new String[]{"Give this a try: `" + wildcard + "`"});
        messages.put(Langs.EIGHT_BALL_OPTIONS, new String[]{"Definitely.", "Without a doubt.", "Yes - of course.", "You can bet on it.", "Most likely.", "It's looking good!", "Duh.", "Signs point to yes.", "Why don't you ask me later?", "Don't count on it.", "My reply is no.", "My sources say no.", "It's not looking good.", "I highly doubt it.", "Nope.", "No way.", "That's a negative."});
        messages.put(Langs.EMOTE_STATS_TITLE, new String[]{"Here are your emoji tallies:"});
        messages.put(Langs.ENHANCE_OPTIONS, new String[]{"Done - " + wildcard + " is now solid gold.", "Done - " + wildcard + " now smells nice.", "Done - " + wildcard + " is now 10GP richer.", "Done - " + wildcard + " won a Nobel Prize.", "Done - " + wildcard + " now has friends.", "Done - " + wildcard + " just made the newspaper", "Done - " + wildcard + " is now part Dragon", "Done - " + wildcard + " now owns the One Ring", "Done - " + wildcard + " is now a wizard, Harry.", "Done - " + wildcard + " came back from the dead.", "Done - " + wildcard + " is now a weeb.", "Done - " + wildcard + " just won the lottery.", "Done - " + wildcard + " now plays Minecraft.", "Done - " + wildcard + " can now rap mad rhymes.", "Done - " + wildcard + "'s ex lover just moved to Madagascar.", "Done - " + wildcard + " is now good at archery.", "Done - " + wildcard + " can now cast magic.", "Done - " + wildcard + " now has a college degree", "Done - " + wildcard + " just invented the lightsaber.", "Done - " + wildcard + " is now radioactive."});
        messages.put(Langs.FAMILY_CREATED, new String[]{"The Family was created! Now let's go add other servers!"});
        messages.put(Langs.FAMILY_JOIN_CODE, new String[]{"**Join Code for " + wildcard + "**" + linebreak + "`" + wildcard + "`" + linebreak + "_Use this one-time code to join a server to the Family._"});
        messages.put(Langs.FAMILY_JOIN_CODE_INVALIDATED, new String[]{"[The Join Code has been invalidated for security purposes]"});
        messages.put(Langs.FAMILY_JOINED, new String[]{"Added **" + wildcard + "** to the **" + wildcard + "** Family!"});
        messages.put(Langs.FAMILY_REMOVED_SERVER, new String[]{"Removed **" + wildcard + "** from the **" + wildcard + "** Family!"});
        messages.put(Langs.FAMILY_SERVER_LIST, new String[]{"The \"" + wildcard + "\" Family contains the following servers:"});
        messages.put(Langs.FEEDBACK_COOLDOWN, new String[]{"Please wait a bit before submitting more feedback."});
        messages.put(Langs.FEEDBACK_SENT, new String[]{":notepad_spiral: Your feedback has been noted. Thanks!" + linebreak + "You can report again in **5 minutes**."});
        messages.put(Langs.FORUM_WEAPON_ADDED_ALIAS, new String[]{"The alias was added!"});
        messages.put(Langs.FORUM_WEAPON_CREATED, new String[]{"Created a new Forum Weapon: **" + wildcard + "**"});
        messages.put(Langs.FORUM_WEAPON_DESTROYED, new String[]{"Weapon destroyed."});
        messages.put(Langs.FORUM_WEAPON_DUPLICATE, new String[]{":warning: Found an existing Forum Weapon with that link: **" + wildcard + "**"});
        messages.put(Langs.FORUM_WEAPON_UPDATED, new String[]{"The weapon was updated with a new link!"});
        messages.put(Langs.FORUM_WEAPONS_PRUNED, new String[]{"Pruned **" + wildcard + "** unused Forum Weapons!"});
        messages.put(Langs.GENERIC_SUCCESS, new String[]{"Success!"});
        messages.put(Langs.HANGMAN_FOOTER_GUESSED, new String[]{"Already guessed:"});
        messages.put(Langs.HANGMAN_TITLE, new String[]{"Let's play Hangman!"});
        messages.put(Langs.INVALID_TRIGGER_TYPE, new String[]{":scream: Invalid trigger type! Your options are: " + wildcard});
        messages.put(Langs.ITEM_ADDED, new String[]{"Added the things :+1:"});
        messages.put(Langs.ITEM_REMOVED, new String[]{"I've removed \"" + wildcard + "\"!"});
        messages.put(Langs.LIST_CREATED, new String[]{"The List was created!"});
        messages.put(Langs.LIST_DELETED, new String[]{"Deleted the `" + wildcard + "` List!"});
        messages.put(Langs.LIST_PRIVACY_TOGGLED, new String[]{"The `" + wildcard + "` List is now `" + wildcard + "`!"});
        messages.put(Langs.MUSIC_ADDED_TO_QUEUE, new String[]{"Added to queue!"});
        messages.put(Langs.MUSIC_LOOPING_TOGGLED, new String[]{"Music looping is now **" + wildcard + "**"});
        messages.put(Langs.MUSIC_LOOPING_QUEUE_TOGGLED, new String[]{"Music Queue looping is now **" + wildcard + "**"});
        messages.put(Langs.MUSIC_PLAYING, new String[]{"Playing!"});
        messages.put(Langs.MUSIC_PLAYING_PLAYLIST, new String[]{"Playing the `" + wildcard + "` Playlist!"});
        messages.put(Langs.MUSIC_PLAYLIST_CREATED, new String[]{"Created a new Playlist `" + wildcard + "`!"});
        messages.put(Langs.MUSIC_PLAYLIST_CONVERTED, new String[]{"Converted the queue into a new Playlist `" + wildcard + "` with `" + wildcard + "` tracks!"});
        messages.put(Langs.MUSIC_PLAYLIST_DELETED, new String[]{"Playlist deleted!"});
        messages.put(Langs.MUSIC_PLAYLIST_TRACK_ADDED, new String[]{"Track added!"});
        messages.put(Langs.MUSIC_PLAYLIST_TRACK_REMOVED, new String[]{"Track removed!"});
        messages.put(Langs.MUSIC_QUEUE_CLEARED, new String[]{"Cleared the music queue!"});
        messages.put(Langs.MUSIC_QUEUE_SHUFFLED, new String[]{"Shuffled the music queue!"});
        messages.put(Langs.MUSIC_QUEUE_REVERSED, new String[]{"Reversed the music queue!"});
        messages.put(Langs.MUSIC_QUEUED_PLAYLIST, new String[]{"Queued the `" + wildcard + "` Playlist!"});
        messages.put(Langs.POLL_TITLE, new String[]{"== POLL =="});
        messages.put(Langs.POLL_TITLE_RESULTS, new String[]{"== POLL (Results) =="});
        messages.put(Langs.RESET_EMOTE_STATS, new String[]{"Emoji usage stats have been reset!"});
        messages.put(Langs.SEARCH_RESULTS, new String[]{"Search results:"});
        messages.put(Langs.SENT_PRIVATE_MESSAGE, new String[]{"I sent some details over in your DMs."});
        messages.put(Langs.SERVER_FAMILY_LIST, new String[]{"This server has joined the following Families:"});
        messages.put(Langs.SETTING_LIST_TITLE, new String[]{"Available settings:"});
        messages.put(Langs.SETTING_UPDATED_SUCCESS, new String[]{"Setting was updated successfully!"});
        messages.put(Langs.TALLY_CURRENT_VALUE, new String[]{"Current tally for `" + wildcard + "`: `" + wildcard + "`"});
        messages.put(Langs.TALLY_LIST, new String[]{"Here's what I have written down:"});
        messages.put(Langs.TALLY_REMOVED, new String[]{"`" + wildcard + "` hast been removed, sire."});
        messages.put(Langs.THOUGHTS_OPTIONS, new String[]{"That's incredible!", "I love it.", "The best thing all week.", "YAAS QUEEN", "Amazing!", "Fantastic :ok_hand:", "I am indifferent.", "Could be better.", "Ick, no way!", "Just no.", "That is offensive.", "I hate that.", "Get that garbage out of my face!"});
        messages.put(Langs.TIMER_STARTED, new String[]{"Started a new :alarm_clock: for " + wildcard});
        messages.put(Langs.TRIGGER_LIST, new String[]{"I'll respond to these things:"});
        messages.put(Langs.WHATSHOULDIDO_INTRO_OPTIONS, new String[]{"I think you should", "I'd love it if you", "My advice is to", "Hmm, perhaps try to", "I know! You should"});
        messages.put(Langs.WHATSHOULDIDO_OPTIONS, new String[]{"defile a grave", "rob a candy store", "deface a subway", "steal a baby's candy", "pirate a low-budget film", "start a riot about gas prices", "rewatch the Star Wars sequels", "curse at an old woman", "donate to a shady charity in Saudi Arabia", "prank call insurance companies", "sell drugs to minors", "write a program in PHP", "narrate an adult audiobook", "swap jobs with Mike Rowe", "start a riot about waiting in traffic", "confuse someone with dementia", "throw eggs at a flock of birds", "rent library books, and return them all sticky", "create a reaction video for YouTube", "invite me to other servers >:}", "sell essential oils", "demand to see the manager", "start a Flat Earth rally", "uncover the truth behind 9/11", "vaguepost on Instagram for attention", "play Madden", "scam impressionable old women out of their retirement funds", "get a life", "kick a puppy", "kick a kitten", "start a 37-tweet rant", "steal art for Karma", "sell out to EA", "text while driving", "watch YouTube Trending", "protest public health guidelines", "talk to the hand", "make smalltalk with the sign-spinner", "drink questionable chemicals", "throw a prom in the McDonalds Playplace"});
        messages.put(Langs.WHEN_OPTIONS, new String[]{"In "+wildcard+" years", "In "+wildcard+" minutes", ""+wildcard+" days ago", "When pigs fly", "Absolutely never", "Right now, but in a parallel universe", "Not sure, ask your mom", ""+wildcard+" years ago", "Once you stop procrastinating", "Once I get elected Chancellor", "After the heat death of the universe", "In precisely "+wildcard+"", "On the next full moon", "When the sand in me hourglass be empty", "Time is subjective", "Time is a tool you can put on the wall", "Probably within "+wildcard+" days", "I'd say in "+wildcard+" months", "In "+wildcard+"? "+wildcard+"? Maybe "+wildcard+"?", "Between "+wildcard+" and "+wildcard+" centuries", "Sooner shall "+wildcard+" days pass", ""+wildcard+" seconds", ""+wildcard+" hours, "+wildcard+" minutes, and "+wildcard+" seconds", "Eventually", "Not in your lifetime, kiddo", "In your dreams", "Right now"});
    }
}