package com.hadenwatne.shmames.models.data;

import com.hadenwatne.shmames.enums.ErrorKeys;

public class LanguageError {
    private final ErrorKeys key;
    private final String[] values;

    public LanguageError(ErrorKeys key, String[] values) {
        this.key = key;
        this.values = values;
    }

    public ErrorKeys getKey() {
        return this.key;
    }

    public String[] getValues() {
        return this.values;
    }
}
