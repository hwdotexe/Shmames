package com.hadenwatne.shmames.language;

public record LanguageError(String key, String[] values) implements Comparable<LanguageError> {

    public int compareTo(LanguageError otherError) {
        return this.key.compareTo(otherError.key());
    }
}
