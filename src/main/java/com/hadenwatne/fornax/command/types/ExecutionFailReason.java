package com.hadenwatne.fornax.command.types;

public enum ExecutionFailReason {
    NONE,
    BOT_MISSING_PERMISSION,
    COMMAND_USAGE_INCORRECT,
    MISSING_INTERACTION_HOOK,
    EXCEPTION_CAUGHT
}
