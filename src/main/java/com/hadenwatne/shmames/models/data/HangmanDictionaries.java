package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.models.game.HangmanDictionary;

import java.util.ArrayList;
import java.util.List;

public class HangmanDictionaries {
    private List<HangmanDictionary> dictionaries;

    public HangmanDictionaries() {
        dictionaries = new ArrayList<>();
    }

    public List<HangmanDictionary> getDictionaries() {
        return this.dictionaries;
    }

    public void loadDefaults() {
        HangmanDictionary defaultDict = new HangmanDictionary("default");

        defaultDict.addWord("shmames", "An awesome bot");
        defaultDict.addWord("memes", "What this bot sends");

        dictionaries.add(defaultDict);
    }
}