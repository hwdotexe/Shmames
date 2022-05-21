package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.enums.ErrorKeys;
import com.hadenwatne.shmames.enums.LanguageKeys;
import com.hadenwatne.shmames.services.RandomService;

import java.util.ArrayList;
import java.util.List;

public class Language {
    public List<LanguageMessage> messages;
    public List<LanguageError> errors;
    public final String wildcard = "%WC%";
    public final String linebreak = "%BR%";
    private String langName;
    private transient String fileName;

    public Language(String name) {
        langName = name;
        messages = new ArrayList<>();
        errors = new ArrayList<>();

        this.fileName = this.langName + ".json";

        populateDefaultValues();
    }

    public String getLangName() {
        return langName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMsg(LanguageKeys key) {
        LanguageMessage message = getLanguageMessage(key);

        if(message != null) {
            String[] messageArray = message.getValues();

            if (messageArray.length > 1) {
                return processBreaks(messageArray[RandomService.GetRandom(messageArray.length)]);
            }

            return processBreaks(messageArray[0]);
        }

        return null;
    }

    public String getMsg(LanguageKeys key, String[] replacements) {
        String msg = getMsg(key);

        for (String r : replacements) {
            // $ characters need to be dynamically escaped, otherwise Java will think it's an improper regex anchor.
            msg = msg.replaceFirst(wildcard, r.replaceAll("\\$", "__DOLLARSIGN__"));
        }

        msg = msg.replaceAll("__DOLLARSIGN__", "\\$");

        return msg;
    }

    public String getError(ErrorKeys key) {
        LanguageError error = getLanguageError(key);

        if(error != null) {
            String[] messageArray = error.getValues();

            if (messageArray.length > 1) {
                return processBreaks(messageArray[RandomService.GetRandom(messageArray.length)]);
            }

            return processBreaks(messageArray[0]);
        }

        return null;
    }

    public LanguageMessage getLanguageMessage(LanguageKeys key) {
        for(LanguageMessage message : messages) {
            if (message.getKey() == key) {
                return message;
            }
        }

        return null;
    }

    public LanguageError getLanguageError(ErrorKeys key) {
        for(LanguageError error : errors) {
            if (error.getKey() == key) {
                return error;
            }
        }

        return null;
    }

    public String getError(ErrorKeys key, String[] replacements) {
        String msg = getError(key);

        for (String r : replacements) {
            msg = msg.replaceFirst(wildcard, r);
        }

        return msg;
    }

    private String processBreaks(String msg) {
        msg = msg.replaceAll(linebreak, System.lineSeparator());

        return msg;
    }

    private void populateDefaultValues() {
        errors.add(new LanguageError(ErrorKeys.ALREADY_EXISTS, new String[]{"That item already exists!"}));
        errors.add(new LanguageError(ErrorKeys.BOT_ERROR, new String[]{"There was an internal error, and your request did not complete."}));
        errors.add(new LanguageError(ErrorKeys.CANNOT_DELETE, new String[]{"Sorry, I can't let you delete that. It's very precious to me."}));
        errors.add(new LanguageError(ErrorKeys.CHANNEL_NOT_FOUND, new String[]{"I can't find the correct channel for that."}));
        errors.add(new LanguageError(ErrorKeys.COMMAND_NOT_FOUND, new String[]{"That command hasn't been invented yet!"}));
        errors.add(new LanguageError(ErrorKeys.FAMILY_ALREADY_EXISTS, new String[]{"You already own a family with that name! Please choose a different name."}));
        errors.add(new LanguageError(ErrorKeys.FAMILY_ALREADY_JOINED, new String[]{"This server already belongs to that family!"}));
        errors.add(new LanguageError(ErrorKeys.FAMILY_INVALID_DETAIL, new String[]{"Invalid Family name or Join Code!"}));
        errors.add(new LanguageError(ErrorKeys.FAMILY_NOT_JOINED, new String[]{"That server has not joined that Family!"}));
        errors.add(new LanguageError(ErrorKeys.FAMILY_MEMBER_MAXIMUM_REACHED, new String[]{"That family has reached the maximum number of servers!"}));
        errors.add(new LanguageError(ErrorKeys.FAMILY_MAXIMUM_REACHED, new String[]{"You can only join up to 3 families!"}));
        errors.add(new LanguageError(ErrorKeys.FAMILY_SERVER_LIST_EMPTY, new String[]{"This Family does not contain any servers."}));
        errors.add(new LanguageError(ErrorKeys.FORUM_WEAPON_MAXIMUM_REACHED, new String[]{"Sorry! I can only keep up to 100 weapons. Please remove some existing weapons before creating more."}));
        errors.add(new LanguageError(ErrorKeys.FORUM_WEAPON_OWNED_OTHER, new String[]{"That weapon is owned by a different server!"}));
        errors.add(new LanguageError(ErrorKeys.GUILD_REQUIRED, new String[]{"That command must be run on a server."}));
        errors.add(new LanguageError(ErrorKeys.HANGMAN_ALREADY_STARTED, new String[]{"There's already a Hangman game in " + wildcard}));
        errors.add(new LanguageError(ErrorKeys.HANGMAN_ALREADY_GUESSED, new String[]{"You've already guessed that letter!"}));
        errors.add(new LanguageError(ErrorKeys.HANGMAN_NOT_STARTED, new String[]{"There isn't a Hangman game running! Try starting one."}));
        errors.add(new LanguageError(ErrorKeys.HEY_THERE, new String[]{"Hey there! Try using `" + wildcard + " help`!"}));
        errors.add(new LanguageError(ErrorKeys.INCORRECT_ITEM_COUNT, new String[]{"Incorrect number of arguments provided!"}));
        errors.add(new LanguageError(ErrorKeys.ITEMS_NOT_FOUND, new String[]{"There weren't any results."}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_NOT_IN_CHANNEL, new String[]{"Please join a voice channel and run this command again."}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_PLAYLIST_ALREADY_EXISTS, new String[]{"A Playlist with that name already exists on this server!"}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_PLAYLIST_DOESNT_EXIST, new String[]{"That Playlist doesn't exist!"}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_PLAYLIST_EMPTY, new String[]{"There are no tracks in that Playlist."}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_PLAYLIST_LIST_EMPTY, new String[]{"There aren't any Playlists yet!"}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_PLAYLIST_NAME_INVALID, new String[]{"Playlist names must be alphanumeric!"}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_PLAYLIST_NAME_MISSING, new String[]{"Please enter a name for the new Playlist."}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_PLAYLIST_PAGE_EMPTY, new String[]{"There are no tracks in that Playlist on this page."}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_PLAYLIST_TRACK_MAXIMUM_REACHED, new String[]{"Playlists currently support a max of 50 tracks."}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_QUEUE_EMPTY, new String[]{"There are no tracks in the Queue."}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_QUEUE_PAGE_EMPTY, new String[]{"There are no tracks in the Queue on this page."}));
        errors.add(new LanguageError(ErrorKeys.MUSIC_WRONG_INPUT, new String[]{"Please enter a media URL or playlist name!"}));
        errors.add(new LanguageError(ErrorKeys.NO_PERMISSION_BOT, new String[]{"I don't have permission to do that on this server."}));
        errors.add(new LanguageError(ErrorKeys.NO_PERMISSION_USER, new String[]{"I'm afraid I can't let you do that."}));
        errors.add(new LanguageError(ErrorKeys.NOT_FOUND, new String[]{"There were no results."}));
        errors.add(new LanguageError(ErrorKeys.PAGE_NOT_FOUND, new String[]{"The page you requested is empty or does not exist."}));
        errors.add(new LanguageError(ErrorKeys.RESERVED_WORD, new String[]{"It looks like you tried to use a reserved word. Try a different one!"}));
        errors.add(new LanguageError(ErrorKeys.SERVER_FAMILY_LIST_EMPTY, new String[]{"This server does not belong to a Family."}));
        errors.add(new LanguageError(ErrorKeys.SETTING_NOT_FOUND, new String[]{"I couldn't find that setting."}));
        errors.add(new LanguageError(ErrorKeys.SETTING_VALUE_INVALID, new String[]{"The value you provided is invalid. Please try again."}));
        errors.add(new LanguageError(ErrorKeys.TIME_VALUE_INCORRECT, new String[]{"The amount of time provided is invalid!"}));
        errors.add(new LanguageError(ErrorKeys.TIMER_LENGTH_INCORRECT, new String[]{"Timers must be set between 1 second and 365 days."}));
        errors.add(new LanguageError(ErrorKeys.TRACK_NOT_PLAYING, new String[]{"There isn't a track playing right now."}));
        errors.add(new LanguageError(ErrorKeys.WRONG_USAGE, new String[]{"The command syntax you used is incorrect."}));

        messages.add(new LanguageMessage(LanguageKeys.ADD_TRIGGER_SUCCESS, new String[]{"I will now send a `" + wildcard + "` response when I hear `" + wildcard + "`!"}));
        messages.add(new LanguageMessage(LanguageKeys.BLAME, new String[]{"I blame " + wildcard}));
        messages.add(new LanguageMessage(LanguageKeys.BLAME_OPTIONS, new String[]{"Obama", "Trump", "Blizzard", "China", "EA", "4Chan", "your mom", "the economy", "Big Pharma", "India", "Nigeria", "Mexico", "Chemtrails", "GMOs", "vaccines", "#VapeLife", "weebs", "essential oils", "Karen", "Epic Games", "video games", "hip hop", "Fortnite", "Source Filmmaker", "Discord", "Coronavirus", "Apple", "Google", "Starbucks", "NASA", "Keanu Reeves", "Oscar the Grouch", "Ohio"}));
        messages.add(new LanguageMessage(LanguageKeys.CHOOSE, new String[]{"I choose: " + wildcard + "!"}));
        messages.add(new LanguageMessage(LanguageKeys.EIGHT_BALL_OPTIONS, new String[]{"Definitely.", "Without a doubt.", "Yes - of course.", "You can bet on it.", "Most likely.", "It's looking good!", "Duh.", "Signs point to yes.", "Why don't you ask me later?", "Don't count on it.", "My reply is no.", "My sources say no.", "It's not looking good.", "I highly doubt it.", "Nope.", "No way.", "That's a negative."}));
        messages.add(new LanguageMessage(LanguageKeys.EMOTE_STATS_TITLE, new String[]{"Here are your emoji tallies:"}));
        messages.add(new LanguageMessage(LanguageKeys.ENHANCE_OPTIONS, new String[]{"Done - " + wildcard + " is now solid gold.", "Done - " + wildcard + " now smells nice.", "Done - " + wildcard + " is now 10GP richer.", "Done - " + wildcard + " won a Nobel Prize.", "Done - " + wildcard + " now has friends.", "Done - " + wildcard + " just made the newspaper", "Done - " + wildcard + " is now part Dragon", "Done - " + wildcard + " now owns the One Ring", "Done - " + wildcard + " is now a wizard, Harry.", "Done - " + wildcard + " came back from the dead.", "Done - " + wildcard + " is now a weeb.", "Done - " + wildcard + " just won the lottery.", "Done - " + wildcard + " now plays Minecraft.", "Done - " + wildcard + " can now rap mad rhymes.", "Done - " + wildcard + "'s ex lover just moved to Madagascar.", "Done - " + wildcard + " is now good at archery.", "Done - " + wildcard + " can now cast magic.", "Done - " + wildcard + " now has a college degree", "Done - " + wildcard + " just invented the lightsaber.", "Done - " + wildcard + " is now radioactive."}));
        messages.add(new LanguageMessage(LanguageKeys.FAMILY_CREATED, new String[]{"The Family was created! Now let's go add other servers!"}));
        messages.add(new LanguageMessage(LanguageKeys.FAMILY_JOIN_CODE, new String[]{"**Join Code for " + wildcard + "**" + linebreak + "`" + wildcard + "`" + linebreak + "_Use this one-time code to join a server to the Family._"}));
        messages.add(new LanguageMessage(LanguageKeys.FAMILY_JOIN_CODE_INVALIDATED, new String[]{"[The Join Code has been invalidated for security purposes]"}));
        messages.add(new LanguageMessage(LanguageKeys.FAMILY_JOINED, new String[]{"Added **" + wildcard + "** to the **" + wildcard + "** Family!"}));
        messages.add(new LanguageMessage(LanguageKeys.FAMILY_REMOVED_SERVER, new String[]{"Removed **" + wildcard + "** from the **" + wildcard + "** Family!"}));
        messages.add(new LanguageMessage(LanguageKeys.FAMILY_SERVER_LIST, new String[]{"The \"" + wildcard + "\" Family contains the following servers:"}));
        messages.add(new LanguageMessage(LanguageKeys.FEEDBACK_COOLDOWN, new String[]{"Please wait a bit before submitting more feedback."}));
        messages.add(new LanguageMessage(LanguageKeys.FEEDBACK_SENT, new String[]{":notepad_spiral: Your feedback has been noted. Thanks!" + linebreak + "You can report again in **5 minutes**."}));
        messages.add(new LanguageMessage(LanguageKeys.FORUM_WEAPON_ADDED_ALIAS, new String[]{"The alias was added!"}));
        messages.add(new LanguageMessage(LanguageKeys.FORUM_WEAPON_CREATED, new String[]{"Created a new Forum Weapon: **" + wildcard + "**"}));
        messages.add(new LanguageMessage(LanguageKeys.FORUM_WEAPON_DESTROYED, new String[]{"Weapon destroyed."}));
        messages.add(new LanguageMessage(LanguageKeys.FORUM_WEAPON_DUPLICATE, new String[]{":warning: Found an existing Forum Weapon with that link: **" + wildcard + "**"}));
        messages.add(new LanguageMessage(LanguageKeys.FORUM_WEAPON_LIST, new String[]{"These are the weapons I found:"}));
        messages.add(new LanguageMessage(LanguageKeys.FORUM_WEAPONS_PRUNED, new String[]{"Pruned **" + wildcard + "** unused Forum Weapons!"}));
        messages.add(new LanguageMessage(LanguageKeys.FORUM_WEAPON_UPDATED, new String[]{"The weapon was updated with a new link!"}));
        messages.add(new LanguageMessage(LanguageKeys.GENERIC_SUCCESS, new String[]{"Success!"}));
        messages.add(new LanguageMessage(LanguageKeys.HANGMAN_DICTIONARIES, new String[]{"Available dictionaries: **" + wildcard + "** (or leave blank to use all of them)"}));
        messages.add(new LanguageMessage(LanguageKeys.HANGMAN_FOOTER_GUESSED, new String[]{"Already guessed:"}));
        messages.add(new LanguageMessage(LanguageKeys.HANGMAN_TITLE, new String[]{"Let's play Hangman!"}));
        messages.add(new LanguageMessage(LanguageKeys.INVALID_TRIGGER_TYPE, new String[]{":scream: Invalid trigger type! Your options are: " + wildcard}));
        messages.add(new LanguageMessage(LanguageKeys.ITEM_ADDED, new String[]{"Added the things :+1:"}));
        messages.add(new LanguageMessage(LanguageKeys.ITEM_REMOVED, new String[]{"I've removed \"" + wildcard + "\"!"}));
        messages.add(new LanguageMessage(LanguageKeys.LIST_CREATED, new String[]{"The List was created!"}));
        messages.add(new LanguageMessage(LanguageKeys.LIST_DELETED, new String[]{"Deleted the `" + wildcard + "` List!"}));
        messages.add(new LanguageMessage(LanguageKeys.LIST_PRIVACY_TOGGLED, new String[]{"The `" + wildcard + "` List is now `" + wildcard + "`!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_ADDED_TO_QUEUE, new String[]{"Added to queue!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_LOOPING_TOGGLED, new String[]{"Music looping is now **" + wildcard + "**"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_LOOPING_QUEUE_TOGGLED, new String[]{"Music Queue looping is now **" + wildcard + "**"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_PLAYING, new String[]{"Playing!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_PLAYING_PLAYLIST, new String[]{"Playing the `" + wildcard + "` Playlist!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_PLAYLIST_CREATED, new String[]{"Created a new Playlist `" + wildcard + "`!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_PLAYLIST_CONVERTED, new String[]{"Converted the queue into a new Playlist `" + wildcard + "` with `" + wildcard + "` tracks!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_PLAYLIST_DELETED, new String[]{"Playlist deleted!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_PLAYLIST_TRACK_ADDED, new String[]{"Track added!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_PLAYLIST_TRACK_REMOVED, new String[]{"Track removed!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_QUEUE_CLEARED, new String[]{"Cleared the music queue!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_QUEUE_SHUFFLED, new String[]{"Shuffled the music queue!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_QUEUE_REVERSED, new String[]{"Reversed the music queue!"}));
        messages.add(new LanguageMessage(LanguageKeys.MUSIC_QUEUED_PLAYLIST, new String[]{"Queued the `" + wildcard + "` Playlist!"}));
        messages.add(new LanguageMessage(LanguageKeys.POLL_TITLE, new String[]{"Cast your vote!"}));
        messages.add(new LanguageMessage(LanguageKeys.POLL_TITLE_RESULTS, new String[]{"The Poll has closed."}));
        messages.add(new LanguageMessage(LanguageKeys.RESET_EMOTE_STATS, new String[]{"Emoji usage stats have been reset!"}));
        messages.add(new LanguageMessage(LanguageKeys.SEARCH_RESULTS, new String[]{"Search results:"}));
        messages.add(new LanguageMessage(LanguageKeys.SENT_PRIVATE_MESSAGE, new String[]{"I sent some details over in your DMs."}));
        messages.add(new LanguageMessage(LanguageKeys.SERVER_FAMILY_LIST, new String[]{"This server has joined the following Families:"}));
        messages.add(new LanguageMessage(LanguageKeys.SETTING_LIST_TITLE, new String[]{"Available settings"}));
        messages.add(new LanguageMessage(LanguageKeys.SETTING_UPDATED_SUCCESS, new String[]{"Setting was updated successfully!"}));
        messages.add(new LanguageMessage(LanguageKeys.STORY_INTRO, new String[]{"Let's read a story!"}));
        messages.add(new LanguageMessage(LanguageKeys.TALLY_CURRENT_VALUE, new String[]{"Current tally for `" + wildcard + "`: `" + wildcard + "`"}));
        messages.add(new LanguageMessage(LanguageKeys.TALLY_LIST, new String[]{"Here's what I have written down:"}));
        messages.add(new LanguageMessage(LanguageKeys.TALLY_REMOVED, new String[]{"`" + wildcard + "` hast been removed, sire."}));
        messages.add(new LanguageMessage(LanguageKeys.THOUGHTS_OPTIONS, new String[]{"That's incredible!", "I love it.", "The best thing all week.", "YAAS QUEEN", "Amazing!", "Fantastic :ok_hand:", "I am indifferent.", "Could be better.", "Ick, no way!", "Just no.", "That is offensive.", "I hate that.", "Get that garbage out of my face!"}));
        messages.add(new LanguageMessage(LanguageKeys.TIMER_STARTED, new String[]{"Started a new :alarm_clock: for " + wildcard}));
        messages.add(new LanguageMessage(LanguageKeys.TIMER_ALERT, new String[]{":alarm_clock: (" + wildcard + "): The timer you set is finished!"}));
        messages.add(new LanguageMessage(LanguageKeys.TRIGGER_LIST, new String[]{"I'll respond to these things:"}));
        messages.add(new LanguageMessage(LanguageKeys.WHAT_ARE_THE_ODDS, new String[]{"About " + wildcard}));
        messages.add(new LanguageMessage(LanguageKeys.WHATSHOULDIDO_INTRO_OPTIONS, new String[]{"I think you should", "I'd love it if you", "My advice is to", "Hmm, perhaps try to", "I know! You should"}));
        messages.add(new LanguageMessage(LanguageKeys.WHATSHOULDIDO_OPTIONS, new String[]{"defile a grave", "rob a candy store", "deface a subway", "steal a baby's candy", "pirate a low-budget film", "start a riot about gas prices", "rewatch the Star Wars sequels", "curse at an old woman", "donate to a shady charity in Saudi Arabia", "prank call insurance companies", "sell drugs to minors", "write a program in PHP", "narrate an adult audiobook", "swap jobs with Mike Rowe", "start a riot about waiting in traffic", "confuse someone with dementia", "throw eggs at a flock of birds", "rent library books, and return them all sticky", "create a reaction video for YouTube", "invite me to other servers >:}", "sell essential oils", "demand to see the manager", "start a Flat Earth rally", "uncover the truth behind 9/11", "vaguepost on Instagram for attention", "play Madden", "scam impressionable old women out of their retirement funds", "get a life", "kick a puppy", "kick a kitten", "start a 37-tweet rant", "steal art for Karma", "sell out to EA", "text while driving", "watch YouTube Trending", "protest public health guidelines", "talk to the hand", "make smalltalk with the sign-spinner", "drink questionable chemicals", "throw a prom in the McDonalds Playplace"}));
        messages.add(new LanguageMessage(LanguageKeys.WHEN_OPTIONS, new String[]{"In "+wildcard+" years", "In "+wildcard+" minutes", ""+wildcard+" days ago", "When pigs fly", "Absolutely never", "Right now, but in a parallel universe", "Not sure, ask your mom", ""+wildcard+" years ago", "Once you stop procrastinating", "Once I get elected Chancellor", "After the heat death of the universe", "In precisely "+wildcard+"", "On the next full moon", "When the sand in me hourglass be empty", "Time is subjective", "Time is a tool you can put on the wall", "Probably within "+wildcard+" days", "I'd say in "+wildcard+" months", "In "+wildcard+"? "+wildcard+"? Maybe "+wildcard+"?", "Between "+wildcard+" and "+wildcard+" centuries", "Sooner shall "+wildcard+" days pass", ""+wildcard+" seconds", ""+wildcard+" hours, "+wildcard+" minutes, and "+wildcard+" seconds", "Eventually", "Not in your lifetime, kiddo", "In your dreams", "Right now"}));
    }
}