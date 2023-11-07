package com.hadenwatne.fornax.service;

import com.hadenwatne.fornax.command.Execution;

public class DefaultLanguageProvider implements ILanguageProvider{
    @Override
    public String getMessageFromKey(String messageKey, String... replacements) {
        return "A language provider has not been configured!";
    }

    @Override
    public String getMessageFromKey(Execution execution, String messageKey, String... replacements) {
        return "A language provider has not been configured!";
    }

    @Override
    public String getErrorFromKey(String errorKey, String... replacements) {
        return "A language provider has not been configured!";
    }

    @Override
    public String getErrorFromKey(Execution execution, String messageKey, String... replacements) {
        return "A language provider has not been configured!";
    }
}
