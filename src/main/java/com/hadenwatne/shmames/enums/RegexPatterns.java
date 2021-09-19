package com.hadenwatne.shmames.enums;

public enum RegexPatterns {
    ALPHANUMERIC("[\\w\\d-_]+"),
    URL("https?:\\/\\/[\\w\\d:/.\\-?&=%#@]+");

    private final String pattern;

    RegexPatterns(String pattern){
        this.pattern = pattern;
    }

    public String getPattern(){
        return pattern;
    }
}
