package com.hadenwatne.fornax.command;

import com.hadenwatne.fornax.App;
import com.hadenwatne.fornax.command.builder.CommandStructure;
import com.hadenwatne.fornax.service.types.LogType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;
public abstract class Command {
    private final CommandStructure commandStructure;
    private final Permission[] botPermissions;
    private final Permission[] enabledUserPermissions;
    private final boolean requiresGuild;
    private final boolean isNSFW;
    private final boolean availableByDefault;
    private final boolean isPro;
    private final List<MessageEmbed.Field> helpFields;

    public Command(boolean requiresGuild) {
        this.commandStructure = this.buildCommandStructure();
        this.botPermissions = this.configureRequiredBotPermissions();
        this.enabledUserPermissions = this.configureEnabledUserPermissions();
        this.requiresGuild = requiresGuild;
        this.isNSFW = false;
        this.availableByDefault = true;
        this.isPro = false;
        this.helpFields = new ArrayList<>();

        configureCommand();
    }

    public Command(boolean requiresGuild, boolean isNSFW) {
        this.commandStructure = this.buildCommandStructure();
        this.botPermissions = this.configureRequiredBotPermissions();
        this.enabledUserPermissions = this.configureEnabledUserPermissions();
        this.requiresGuild = requiresGuild;
        this.isNSFW = isNSFW;
        this.availableByDefault = true;
        this.isPro = false;
        this.helpFields = new ArrayList<>();

        configureCommand();
    }

    public Command(boolean requiresGuild, boolean isNSFW, boolean availableByDefault, boolean isPro) {
        this.commandStructure = this.buildCommandStructure();
        this.botPermissions = this.configureRequiredBotPermissions();
        this.enabledUserPermissions = this.configureEnabledUserPermissions();
        this.requiresGuild = requiresGuild;
        this.isNSFW = isNSFW;
        this.availableByDefault = availableByDefault;
        this.isPro = isPro;
        this.helpFields = new ArrayList<>();

        configureCommand();
    }

    public CommandStructure getCommandStructure() {
        return this.commandStructure;
    }

    public Permission[] getRequiredPermissions() {
        return this.botPermissions;
    }

    public Permission[] getEnabledUserPermissions() {
        return enabledUserPermissions;
    }

    public boolean requiresGuild() {
        return this.requiresGuild;
    }

    public boolean isNSFW() {
        return this.isNSFW;
    }

    public List<MessageEmbed.Field> getHelpFields() {
        return this.helpFields;
    }

    public boolean isAvailableByDefault() {
        return availableByDefault;
    }

    public boolean isPro() {
        return isPro;
    }

    protected abstract CommandStructure buildCommandStructure();
    protected abstract Permission[] configureRequiredBotPermissions();
    protected abstract Permission[] configureEnabledUserPermissions();
    public abstract void onCommandFailure(Execution execution);
    public abstract void run(Execution execution);

    private void configureCommand() {
        // Build our command's Help fields.
        this.helpFields.add(new MessageEmbed.Field("Server-only", this.requiresGuild ? "Yes" : "No", true));
        this.helpFields.add(new MessageEmbed.Field("Description", this.commandStructure.getDescription(), false));
        this.helpFields.add(new MessageEmbed.Field("Usage", this.commandStructure.getUsage(), true));
        this.helpFields.add(new MessageEmbed.Field("Examples", this.commandStructure.getExamples(), true));

        App.getLogger().Log(LogType.SYSTEM, "\tLoaded " + this.commandStructure.getName());
    }
}