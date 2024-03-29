package com.hadenwatne.shmames.models.game;

import com.hadenwatne.shmames.services.RandomService;

import java.util.HashMap;

public class HangmanDictionary {
    private String name;
    private HashMap<String, String> words;

    public HangmanDictionary(String name){
        this.name = name;
        this.words = new HashMap<String, String>(){};
    }

    public String getName(){
        return this.name;
    }

    public HashMap<String, String> getWords(){
        return this.words;
    }

    public void addWord(String word, String hint){
        this.words.put(word.toLowerCase(), hint);
    }

    public String randomWord(){
        return RandomService.GetRandomFromSet(words.keySet());
    }
}
