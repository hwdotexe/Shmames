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
        errors.add(new LanguageError(ErrorKey.MISSING_BOT_PERMISSION.name(), new String[]{"Please adjust my bot permissions to include:"+linebreak+wildcard}));
        errors.add(new LanguageError(ErrorKey.WRONG_USAGE.name(), new String[]{"Whoops! Incorrect usage - please try again."}));

        messages.add(new LanguageMessage(LanguageKey.BLAME.name(), new String[]{"I blame " + wildcard}));
        messages.add(new LanguageMessage(LanguageKey.BLAME_OPTIONS.name(), new String[]{"Obama", "Trump", "Blizzard", "China", "EA", "4Chan", "your mom", "the economy", "Big Pharma", "India", "Nigeria", "Mexico", "Chemtrails", "GMOs", "vaccines", "#VapeLife", "weebs", "essential oils", "Karen", "Epic Games", "video games", "hip hop", "Fortnite", "Source Filmmaker", "Discord", "Coronavirus", "Apple", "Google", "Starbucks", "NASA", "Keanu Reeves", "Oscar the Grouch", "Ohio"}));
        messages.add(new LanguageMessage(LanguageKey.CHOOSE.name(), new String[]{"I choose: " + wildcard + "!"}));
        messages.add(new LanguageMessage(LanguageKey.EIGHT_BALL_OPTIONS.name(), new String[]{"Definitely.", "Without a doubt.", "Yes - of course.", "You can bet on it.", "Most likely.", "It's looking good!", "Duh.", "Signs point to yes.", "Why don't you ask me later?", "Don't count on it.", "My reply is no.", "My sources say no.", "It's not looking good.", "I highly doubt it.", "Nope.", "No way.", "That's a negative."}));
        messages.add(new LanguageMessage(LanguageKey.GENERIC_SUCCESS.name(), new String[]{"Success!"}));
        messages.add(new LanguageMessage(LanguageKey.ODDS.name(), new String[]{"About " + wildcard + "%", "Roughly " + wildcard + "%"}));
    }
}