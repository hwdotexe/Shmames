package com.hadenwatne.botcore.service;

public class DefaultLanguageProvider implements ILanguageProvider{
    @Override
    public String getMessageFromKey(String messageKey, String... replacements) {
        return "A language provider has not been configured!";
    }

    @Override
    public String getErrorFromKey(String errorKey, String... replacements) {
        return "A language provider has not been configured!";
    }
}
