package com.hadenwatne.shmames.language;

public record LanguageMessage(String key, String[] values) implements Comparable<LanguageMessage> {

    public int compareTo(LanguageMessage otherMessage) {
        return this.key.compareTo(otherMessage.key());
    }
}
