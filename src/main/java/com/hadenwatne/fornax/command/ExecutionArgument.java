package com.hadenwatne.fornax.command;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class ExecutionArgument {
    private String _name;
    private OptionType _type;

    public ExecutionArgument(OptionMapping optionMapping) {
        this._name = optionMapping.getName();
        this._type = optionMapping.getType();

    }
}
