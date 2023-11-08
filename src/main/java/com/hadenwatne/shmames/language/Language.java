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
        errors.add(new LanguageError(ErrorKey.GENERIC_ERROR.name(), new String[]{"Something went wrong! Please try again later."}));
        errors.add(new LanguageError(ErrorKey.WRONG_USAGE.name(), new String[]{"Whoops! Incorrect usage - please try again."}));

        messages.add(new LanguageMessage(LanguageKey.BLAME.name(), new String[]{"I blame " + wildcard}));
        messages.add(new LanguageMessage(LanguageKey.BLAME_OPTIONS.name(), new String[]{"Obama", "Trump", "Blizzard", "China", "EA", "4Chan", "your mom", "the economy", "Big Pharma", "India", "Nigeria", "Mexico", "Chemtrails", "GMOs", "vaccines", "#VapeLife", "weebs", "essential oils", "Karen", "Epic Games", "video games", "hip hop", "Fortnite", "Source Filmmaker", "Discord", "Coronavirus", "Apple", "Google", "Starbucks", "NASA", "Keanu Reeves", "Oscar the Grouch", "Ohio"}));
        messages.add(new LanguageMessage(LanguageKey.CHOOSE.name(), new String[]{"I choose: " + wildcard + "!"}));
    }
}