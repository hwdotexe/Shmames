package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.enums.LanguageKeys;

public class LanguageMessage {
    private final LanguageKeys key;
    private final String[] values;

    public LanguageMessage(LanguageKeys key, String[] values) {
        this.key = key;
        this.values = values;
    }

    public LanguageKeys getKey() {
        return this.key;
    }

    public String[] getValues() {
        return this.values;
    }
}
