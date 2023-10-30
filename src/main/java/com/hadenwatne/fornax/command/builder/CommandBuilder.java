package com.hadenwatne.fornax.command.builder;

import com.hadenwatne.fornax.command.Command;
import com.hadenwatne.fornax.command.builder.types.ParameterType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.util.List;

public class CommandBuilder {
    public static CommandStructure Create(String name, String description) {
        return new CommandStructure(name, description);
    }

    public static SlashCommandData BuildSlashCommandData(Command command) {
        CommandStructure structure = command.getCommandStructure();
        String description = structure.getDescription();
        String shortDesc = description.length() > 100 ? (description.substring(0, 96) + "...") : description;
        SlashCommandData data = Commands.slash(structure.getName(), shortDesc);

        data.setGuildOnly(command.requiresGuild());
        data.setNSFW(command.isNSFW());
        data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(command.getEnabledUserPermissions()));

        // If there are subcommands, add these instead.
        if (!structure.getSubCommands().isEmpty() || !structure.getSubcommandGroups().isEmpty()) {
            // Add SubCommands.
            for (CommandStructure subCommand : structure.getSubCommands()) {
                String subShortDesc = subCommand.getDescription().length() > 100 ? (subCommand.getDescription().substring(0, 96) + "...") : subCommand.getDescription();
                SubcommandData subCommandData = new SubcommandData(subCommand.getName(), subShortDesc);

                // Build sub command's parameter data.
                for (CommandParameter p : subCommand.getParameters()) {
                    OptionData option = buildCommandOptionData(p);

                    subCommandData.addOptions(option);
                }

                data.addSubcommands(subCommandData);
            }

            // Add SubCommand groups.
            for(SubCommandGroup subCommandGroup : structure.getSubcommandGroups()) {
                SubcommandGroupData group = new SubcommandGroupData(subCommandGroup.getName(), subCommandGroup.getDescription());

                for (CommandStructure subCommand : subCommandGroup.getSubCommands()) {
                    String subShortDesc = subCommand.getDescription().length() > 100 ? (subCommand.getDescription().substring(0, 96) + "...") : subCommand.getDescription();
                    SubcommandData subCommandData = new SubcommandData(subCommand.getName(), subShortDesc);

                    // Build sub command's parameter data.
                    for (CommandParameter p : subCommand.getParameters()) {
                        OptionData option = buildCommandOptionData(p);

                        subCommandData.addOptions(option);
                    }

                    group.addSubcommands(subCommandData);
                }

                data.addSubcommandGroups(group);
            }
        } else {
            // Build primary command's parameter data.
            for (CommandParameter p : structure.getParameters()) {
                OptionData option = buildCommandOptionData(p);

                data.addOptions(option);
            }
        }

        return data;
    }

    public static String BuildUsage(CommandStructure command, boolean boldCommandName) {
        StringBuilder sb = new StringBuilder();
        List<CommandStructure> subCommands = command.getSubCommands();
        List<SubCommandGroup> subCommandGroups = command.getSubcommandGroups();

        if(boldCommandName) {
            sb.append("**");
        }

        sb.append(command.getName());

        if(boldCommandName) {
            sb.append("**");
        }

        // Add subcommands and groups.
        if(!subCommands.isEmpty() || !subCommandGroups.isEmpty()) {
            sb.append("...");
            sb.append("\n• ");

            if(!subCommands.isEmpty()) {
                sb.append(buildSubCommandUsageLabel(subCommands));
            }

            if(!subCommandGroups.isEmpty()) {
                if(!subCommands.isEmpty()) {
                    sb.append("\n• ");
                }

                sb.append(buildSubCommandGroupUsageLabel(subCommandGroups));
            }
        }

        // Now add any command parameters.
        for (CommandParameter p : command.getParameters()) {
            sb.append(" ");

            if (p.isRequired()) {
                sb.append("<");
                sb.append(buildUsageLabel(p));
                sb.append(">");
            } else {
                sb.append("_");
                sb.append("[");
                sb.append(buildUsageLabel(p));
                sb.append("]");
                sb.append("_");
            }
        }

        return sb.toString();
    }

    public static String BuildExample(CommandStructure command) {
        StringBuilder example = new StringBuilder();
        List<CommandStructure> subCommands = command.getSubCommands();
        List<SubCommandGroup> subCommandGroups = command.getSubcommandGroups();

        example.append(command.getName());

        if(subCommands.size() > 0 || subCommandGroups.size() > 0) {
            example.append("...");

            for(CommandStructure subCommand : subCommands) {
                example.append(System.lineSeparator());

                example.append(command.getName());
                example.append(" ");
                example.append(subCommand.getName());

                for(CommandParameter parameter : subCommand.getParameters()) {
                    example.append(" ");
                    example.append(parameter.getExample());
                }
            }

            for(SubCommandGroup subCommandGroup: subCommandGroups) {
                for(CommandStructure subCommand : subCommandGroup.getSubCommands()) {
                    example.append(System.lineSeparator());

                    example.append(command.getName());
                    example.append(" ");
                    example.append(subCommandGroup.getName());
                    example.append(" ");
                    example.append(subCommand.getName());

                    for(CommandParameter parameter : subCommand.getParameters()) {
                        if(parameter.getExample() != null && parameter.getExample().length() > 0) {
                            example.append(" ");
                            example.append(parameter.getExample());
                        }
                    }
                }
            }
        } else {
            for(CommandParameter parameter : command.getParameters()) {
                example.append(" ");
                example.append(parameter.getExample());
            }
        }

        return example.toString();
    }

    private static OptionData buildCommandOptionData(CommandParameter p) {
        OptionData option = new OptionData(MapParameterType(p.getType()), p.getName().toLowerCase(), p.getDescription())
                .setRequired(p.isRequired());

        if (!p.getSelectionOptions().isEmpty()) {
            for (String so : p.getSelectionOptions()) {
                option.addChoice(so, so);
            }
        }

        return option;
    }

    private static String buildSubCommandUsageLabel(List<CommandStructure> subCommands) {
        StringBuilder subCommandData = new StringBuilder();

        for(CommandStructure subCommand : subCommands) {
            if(subCommandData.length() > 0) {
                subCommandData.append("\n• ");
            }

            subCommandData.append(BuildUsage(subCommand, true));
        }

        return subCommandData.toString();
    }

    private static String buildSubCommandGroupUsageLabel(List<SubCommandGroup> subCommandGroups) {
        StringBuilder subCommandGroupData = new StringBuilder();

        for(SubCommandGroup group : subCommandGroups) {
            StringBuilder subCommandData = new StringBuilder();

            if (subCommandGroupData.length() > 0) {
                subCommandGroupData.append("\n• ");
            }

            for (CommandStructure subCommand : group.getSubCommands()) {
                if (subCommandData.length() > 0) {
                    subCommandData.append("\n• ");
                }

                subCommandData.append("**");
                subCommandData.append(group.getName());
                subCommandData.append("**");
                subCommandData.append(" ");
                subCommandData.append(BuildUsage(subCommand, true));
            }

            subCommandGroupData.append(subCommandData);
        }

        return subCommandGroupData.toString();
    }

    private static String buildUsageLabel(CommandParameter p) {
        StringBuilder sb = new StringBuilder();

        if(p.getType() == ParameterType.SELECTION) {
            sb.append("[");

            StringBuilder ssb = new StringBuilder();

            for(String s : p.getSelectionOptions()) {
                if(ssb.length() > 0) {
                    ssb.append(" | ");
                }

                ssb.append(s);
            }

            sb.append(ssb);
            sb.append("]");
        } else if(p.getType() == ParameterType.TIMECODE) {
            sb.append("#[y|d|h|m|s]");
        } else {
            sb.append(p.getName());
        }

        return sb.toString();
    }

    private static OptionType MapParameterType(ParameterType type) {
        switch(type) {
            case INTEGER:
                return OptionType.INTEGER;
            case BOOLEAN:
                return OptionType.BOOLEAN;
            case DISCORD_ROLE:
                return OptionType.ROLE;
            case DISCORD_USER:
                return OptionType.USER;
            case DISCORD_CHANNEL:
                return OptionType.CHANNEL;
            default:
                return OptionType.STRING;
        }
    }
}
