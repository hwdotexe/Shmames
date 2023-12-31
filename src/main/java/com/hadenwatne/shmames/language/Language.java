package com.hadenwatne.shmames.language;

import com.hadenwatne.shmames.services.RandomService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Language {
    public List<LanguageMessage> messages;
    public List<LanguageError> errors;
    public final String wildcard = "%WC%";
    public final String linebreak = "%BR%";
    private final String languageName;
    private transient String fileName;

    public Language(String name) {
        languageName = name;
        messages = new ArrayList<>();
        errors = new ArrayList<>();

        this.fileName = this.languageName + ".json";

        populateDefaultValues();
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMessage(String messageKey, String... replacements) {
        for (LanguageMessage languageMessage : messages) {
            if (languageMessage.key().equalsIgnoreCase(messageKey)) {
                String[] messageArray = languageMessage.values();
                String message = messageArray[RandomService.GetRandom(messageArray.length)];

                // Perform any necessary transforms.
                message = message.replaceAll(linebreak, System.lineSeparator());

                for (String r : replacements) {
                    message = message.replaceFirst(wildcard, Matcher.quoteReplacement(r));
                }

                return message;
            }
        }

        return null;
    }

    public String getError(String errorKey, String... replacements) {
        for (LanguageError languageError : errors) {
            if (languageError.key().equalsIgnoreCase(errorKey)) {
                String[] messageArray = languageError.values();
                String message = messageArray[RandomService.GetRandom(messageArray.length)];

                // Perform any necessary transforms.
                message = message.replaceAll(linebreak, System.lineSeparator());

                for (String r : replacements) {
                    message = message.replaceFirst(wildcard, Matcher.quoteReplacement(r));
                }

                return message;
            }
        }

        return null;
    }

    private void populateDefaultValues() {
        errors.add(new LanguageError(ErrorKey.ANSWER_NOT_FOUND.name(), new String[]{"Sorry! I couldn't find anything on that."}));
        errors.add(new LanguageError(ErrorKey.GENERIC_ERROR.name(), new String[]{"Something went wrong! Please try again later."}));
        errors.add(new LanguageError(ErrorKey.MISSING_BOT_PERMISSION.name(), new String[]{"Please adjust my bot permissions to include:" + linebreak + wildcard}));
        errors.add(new LanguageError(ErrorKey.POLL_ITEM_COUNT_INCORRECT.name(), new String[]{"Please include at least 2 poll options, separated by `;`."}));
        errors.add(new LanguageError(ErrorKey.TALLY_NOT_FOUND.name(), new String[]{"That tally name couldn't be found!"}));
        errors.add(new LanguageError(ErrorKey.TIME_VALUE_INCORRECT.name(), new String[]{"The amount of time provided is invalid!"}));
        errors.add(new LanguageError(ErrorKey.WRONG_USAGE.name(), new String[]{"Whoops! Incorrect usage - please try again."}));

        messages.add(new LanguageMessage(LanguageKey.BLAME.name(), new String[]{"I blame " + wildcard}));
        messages.add(new LanguageMessage(LanguageKey.BLAME_OPTIONS.name(), new String[]{"Obama", "Trump", "Blizzard", "China", "EA", "4Chan", "your mom", "the economy", "Big Pharma", "India", "Nigeria", "Mexico", "Chemtrails", "GMOs", "vaccines", "#VapeLife", "weebs", "essential oils", "Karen", "Epic Games", "video games", "hip hop", "Fortnite", "Source Filmmaker", "Discord", "Coronavirus", "Apple", "Google", "Starbucks", "NASA", "Keanu Reeves", "Oscar the Grouch", "Ohio"}));
        messages.add(new LanguageMessage(LanguageKey.CHOOSE.name(), new String[]{"I choose: " + wildcard + "!"}));
        messages.add(new LanguageMessage(LanguageKey.EIGHT_BALL_OPTIONS.name(), new String[]{"Definitely.", "Without a doubt.", "Yes - of course.", "You can bet on it.", "Most likely.", "It's looking good!", "Duh.", "Signs point to yes.", "Why don't you ask me later?", "Don't count on it.", "My reply is no.", "My sources say no.", "It's not looking good.", "I highly doubt it.", "Nope.", "No way.", "That's a negative."}));
        messages.add(new LanguageMessage(LanguageKey.GENERIC_SUCCESS.name(), new String[]{"Success!"}));
        messages.add(new LanguageMessage(LanguageKey.TALLIES_CLEARED.name(), new String[]{"The tally list has been reset!"}));
        messages.add(new LanguageMessage(LanguageKey.ODDS.name(), new String[]{"About " + wildcard + "%", "Roughly " + wildcard + "%"}));
        messages.add(new LanguageMessage(LanguageKey.TALLY_CURRENT_VALUE.name(), new String[]{"Current tally for `" + wildcard + "`: `" + wildcard + "`"}));
        messages.add(new LanguageMessage(LanguageKey.TALLY_LIST.name(), new String[]{"These are the tallies I have written down:"}));
        messages.add(new LanguageMessage(LanguageKey.TALLY_REMOVED.name(), new String[]{"The `" + wildcard + "` tally has been removed!"}));
        messages.add(new LanguageMessage(LanguageKey.TIMER_ALERT.name(), new String[]{"Hey " + wildcard + " - your timer is done!"}));
        messages.add(new LanguageMessage(LanguageKey.TIMER_STARTED.name(), new String[]{"Timer started for " + wildcard}));
        messages.add(new LanguageMessage(LanguageKey.WHATSHOULDIDO.name(), new String[]{"I think you should", "I'd love it if you", "My advice is to", "Hmm, perhaps try to", "I know! You should"}));
        messages.add(new LanguageMessage(LanguageKey.WHATSHOULDIDO_OPTIONS.name(), new String[]{"defile a grave", "rob a candy store", "deface a subway", "steal a baby's candy", "pirate a low-budget film", "start a riot about gas prices", "rewatch the Star Wars sequels", "curse at an old woman", "donate to a shady charity in Saudi Arabia", "prank call insurance companies", "sell drugs to minors", "write a program in PHP", "narrate an adult audiobook", "swap jobs with Mike Rowe", "start a riot about waiting in traffic", "confuse someone with dementia", "throw eggs at a flock of birds", "rent library books, and return them all sticky", "create a reaction video for YouTube", "invite me to other servers >:}", "sell essential oils", "demand to see the manager", "start a Flat Earth rally", "uncover the truth behind 9/11", "vaguepost on Instagram for attention", "play Madden", "scam impressionable old women out of their retirement funds", "get a life", "kick a puppy", "kick a kitten", "start a 37-tweet rant", "steal art for Karma", "sell out to EA", "text while driving", "watch YouTube Trending", "protest public health guidelines", "talk to the hand", "make smalltalk with the sign-spinner", "drink questionable chemicals", "throw a prom in the McDonalds Playplace"}));
        messages.add(new LanguageMessage(LanguageKey.WHEN_OPTIONS.name(), new String[]{"In " + wildcard + " years", "In " + wildcard + " minutes", wildcard + " days ago", "When pigs fly", "Absolutely never", "Right now, but in a parallel universe", "Not sure, ask your mom", wildcard + " years ago", "Once you stop procrastinating", "Once I get elected Chancellor", "After the heat death of the universe", "In precisely " + wildcard, "On the next full moon", "When the sand in me hourglass be empty", "Time is subjective", "Time is a tool you can put on the wall", "Probably within " + wildcard + " days", "I'd say in " + wildcard + " months", "In " + wildcard, "Sooner shall " + wildcard + " days pass", wildcard + " seconds", wildcard + " hours", "Eventually", "Not in your lifetime, kiddo", "In your dreams", "Right now"}));
    }
}