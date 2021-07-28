package com.hadenwatne.shmames.commandbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CommandStructure {
    private String name;
    private List<String> aliases;
    private List<CommandParameter> parameters;

    public CommandStructure(String name) {
        this.name = name;
        this.aliases = new ArrayList<>();
        this.parameters = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public CommandStructure addAlias(String alias) {
        this.aliases.add(alias);

        return this;
    }

    public List<CommandParameter> getParameters() {
        return this.parameters;
    }

    public CommandStructure addParameter(CommandParameter parameter) {
        this.parameters.add(parameter);

        return this;
    }

    public CommandStructure addParameters(CommandParameter... parameters) {
        Collections.addAll(this.parameters, parameters);

        return this;
    }
}
