package com.hadenwatne.fornax.service;

public interface ILanguageProvider {
    String getMessageFromKey(String messageKey, String... replacements);
    String getErrorFromKey(String errorKey, String... replacements);
}
