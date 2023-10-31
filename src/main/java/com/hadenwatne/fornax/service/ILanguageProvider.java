package com.hadenwatne.fornax.service;

import com.hadenwatne.fornax.command.Execution;

public interface ILanguageProvider {
    String getMessageFromKey(String messageKey, String... replacements);
    String getMessageFromKey(Execution execution, String messageKey, String... replacements);
    String getErrorFromKey(String errorKey, String... replacements);
    String getErrorFromKey(Execution execution, String messageKey, String... replacements);
}
