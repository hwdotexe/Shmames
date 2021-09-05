package com.hadenwatne.shmames.commandbuilder;

import com.hadenwatne.shmames.commands.ICommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.List;
import java.util.regex.Pattern;

public class CommandBuilder {
    public static CommandStructure Create(String name, String description) {
        return new CommandStructure(name, description);
    }

    public static CommandData BuildCommandData(ICommand command) {
        CommandStructure structure = command.getCommandStructure();
        String description = structure.getDescription();
        String shortDesc = description.length() > 100 ? (description.substring(0, 96) + "...") : description;
        CommandData data = new CommandData(structure.getName(), shortDesc);

        // If there are subcommands, add these instead.
        if (structure.getSubcommands().size() > 0 || structure.getSubcommandGroups().size() > 0) {
            // Add SubCommands.
            for (CommandStructure subCommand : structure.getSubcommands()) {
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

                for (CommandStructure subCommand : subCommandGroup.getSubcommands()) {
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

    private static OptionData buildCommandOptionData(CommandParameter p) {
        OptionData option = new OptionData(MapParameterType(p.getType()), p.getName().toLowerCase(), p.getDescription())
                .setRequired(p.isRequired());

        if (p.getSelectionOptions().size() > 0) {
            for (String so : p.getSelectionOptions()) {
                option.addChoice(so, so);
            }
        }

        return option;
    }

    public static Pattern BuildPattern(CommandStructure command) {
        StringBuilder sb = new StringBuilder();

        // Build pattern for subCommandGroups.
        boolean hasSubCommandGroups = command.getSubcommandGroups().size() > 0;

        if(hasSubCommandGroups) {
            for(SubCommandGroup group : command.getSubcommandGroups()) {
                if(sb.length() > 0) {
                    sb.append("|");
                }

                sb.append("(");
                sb.append(group.getName());
                sb.append("\\s");
                sb.append("(");
                sb.append(BuildSubCommandPattern(group.getSubcommands()));
                sb.append("))");
            }
        }

        // Make sure that additional SubCommands are OR'd properly.
        if(hasSubCommandGroups) {
            sb.append("|(");
        }

        // Build pattern for subcommands.
        if (command.getSubcommands().size() > 0) {
            sb.append(BuildSubCommandPattern(command.getSubcommands()));
        }

        // If the command has parameters of its own, add them after.
        sb.append(BuildParameterPattern(command));

        // Make sure that additional SubCommands are OR'd properly.
        if(hasSubCommandGroups) {
            sb.append(")");
        }

        // Seal the pattern to match the whole string.
        sb.insert(0, "^");
        sb.append("$");

        return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
    }

    private static String BuildSubCommandPattern(List<CommandStructure> subCommands) {
        StringBuilder scb = new StringBuilder();

        for (CommandStructure subCommand : subCommands) {
            if(scb.length() > 0) {
                scb.append("|");
            }

            // Begins the subcommand group.
            scb.append("(");

            // Add subcommand name & aliases
            if(subCommand.getAliases().size() > 0) {
                // Begin a group to OR each name and alias.
                scb.append("((");
                scb.append(subCommand.getName());
                scb.append(")");

                // Add each additional alias as an OR group.
                for(String alias : subCommand.getAliases()) {
                    scb.append("|(");
                    scb.append(alias);
                    scb.append(")");
                }

                // End the alias group
                scb.append(")");
            } else {
                scb.append(subCommand.getName());
            }

            // Add subcommand parameters
            if(subCommand.getParameters().size() > 0) {
                boolean anySubCommandParameterRequired = subCommand.getParameters().stream().anyMatch(CommandParameter::isRequired);

                if(!anySubCommandParameterRequired) {
                    scb.append("(");
                }

                scb.append("\\s");
                scb.append(BuildParameterPattern(subCommand));

                if(!anySubCommandParameterRequired) {
                    scb.append(")?");
                }
            }

            // Ends the subcommand group.
            scb.append(")");
        }

        return scb.toString();
    }

    private static String BuildParameterPattern(CommandStructure command) {
        StringBuilder sb = new StringBuilder();

        for(CommandParameter p : command.getParameters()) {
            if(sb.length() > 0) {
                if(!p.isRequired()) {
                    sb.append("(\\s");
                    sb.append(p.getPattern().pattern());
                    sb.append(")?");
                    continue;
                }else{
                    sb.append("\\s");
                }
            }

            sb.append(p.getPattern().pattern());

            if(!p.isRequired()) {
                sb.append("?");
            }
        }

        return sb.toString();
    }

    // TODO update for subcommands
    public static String BuildUsage(CommandStructure command) {
        StringBuilder sb = new StringBuilder();
        List<CommandStructure> subCommands = command.getSubcommands();

        sb.append(command.getName());

        if(subCommands.size() > 0) {
            sb.append(" ");

            if(subCommands.size() > 1) {
                sb.append("<[");
            }

            sb.append(buildSubCommandUsageLabel(subCommands));

            if(subCommands.size() > 1) {
                sb.append("]>");
            }
        } else {
            for (CommandParameter p : command.getParameters()) {
                sb.append(" ");

                if (p.isRequired()) {
                    sb.append("<");
                    sb.append(buildUsageLabel(p));
                    sb.append(">");
                } else {
                    sb.append("[");
                    sb.append(buildUsageLabel(p));
                    sb.append("]");
                }
            }
        }

        return sb.toString();
    }

    private static String buildSubCommandUsageLabel(List<CommandStructure> subCommands) {
        StringBuilder subCommandData = new StringBuilder();

        for(CommandStructure subCommand : subCommands) {
            if(subCommandData.length() > 0) {
                subCommandData.append(" | ");
            }

            subCommandData.append(BuildUsage(subCommand));
        }

        return subCommandData.toString();
    }

    private static String buildUsageLabel(CommandParameter p) {
        StringBuilder sb = new StringBuilder();

        if(p.getType() == ParameterType.SELECTION) {
            sb.append("[");

            StringBuilder ssb = new StringBuilder();

            for(String s : p.getSelectionOptions()) {
                if(ssb.length() > 0) {
                    ssb.append("|");
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
            case DISCORD_EMOTE:
                return OptionType.MENTIONABLE;
            case DISCORD_CHANNEL:
                return OptionType.CHANNEL;
            default:
                return OptionType.STRING;
        }
    }
}
