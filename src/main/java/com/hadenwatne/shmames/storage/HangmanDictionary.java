package com.hadenwatne.shmames.storage;

import com.hadenwatne.shmames.Utils;

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
        return words.keySet().toArray()[Utils.getRandom(words.keySet().size())].toString();
    }
}
