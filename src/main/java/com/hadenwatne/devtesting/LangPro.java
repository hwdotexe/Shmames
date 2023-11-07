package com.hadenwatne.devtesting;

import com.hadenwatne.fornax.command.Execution;
import com.hadenwatne.fornax.service.ILanguageProvider;

public class LangPro implements ILanguageProvider {
    @Override
    public String getMessageFromKey(String messageKey, String... replacements) {
        return "Lang is Pro, bro.";
    }

    @Override
    public String getMessageFromKey(Execution execution, String messageKey, String... replacements) {
        return "Lang is Pro, bro.";
    }

    @Override
    public String getErrorFromKey(String errorKey, String... replacements) {
        return "Lang is Pro, bro.";
    }

    @Override
    public String getErrorFromKey(Execution execution, String messageKey, String... replacements) {
        return "Lang is Pro, bro.";
    }
}
