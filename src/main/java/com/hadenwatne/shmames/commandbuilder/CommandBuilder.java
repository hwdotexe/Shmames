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
    public static CommandStructure Create(String name) {
        return new CommandStructure(name);
    }

    public static CommandData BuildCommandData(ICommand command) {
        CommandStructure structure = command.getCommandStructure();
        String shortDesc = command.getDescription().length() > 100 ? (command.getDescription().substring(0, 96) + "...") : command.getDescription();
        CommandData data = new CommandData(structure.getName(), shortDesc);

        // If there are subcommands, add these instead.
        // TODO JDA/Discord limit the ability to have the base command with a mix of parameters and subcommands.
        if (structure.getSubcommands().size() > 0) {
            for (CommandStructure subCommand : structure.getSubcommands()) {
                String subShortDesc = command.getDescription().length() > 100 ? (command.getDescription().substring(0, 96) + "...") : command.getDescription();
                SubcommandData subCommandData = new SubcommandData(subCommand.getName(), subShortDesc);

                // Build sub command's parameter data.
                for (CommandParameter p : subCommand.getParameters()) {
                    OptionData option = buildCommandOptionData(p);

                    subCommandData.addOptions(option);
                }

                data.addSubcommands(subCommandData);
            }
        } else {
            // TODO if they ever let us combine these, this block can stand on its own.
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
        List<CommandStructure> subCommands = command.getSubcommands();

        // Build pattern for subcommands first
        if (command.getSubcommands().size() > 0) {
            boolean anySubCommandsRequired = subCommands.stream().anyMatch(sc -> !sc.isOptional());

            if(!anySubCommandsRequired) {
                sb.append("(");
            }

            StringBuilder scb = new StringBuilder();
            for (CommandStructure subCommand : subCommands) {
                if(scb.length() > 0) {
                    scb.append("|");
                }

                scb.append("(");
                scb.append(subCommand.getName());

                if(subCommand.getParameters().size() > 0) {
                    scb.append("\\s");
                    scb.append(BuildParameterPattern(subCommand));
                }

                scb.append(")");
            }

            sb.append(scb);

            if(!anySubCommandsRequired) {
                sb.append("\\s)?");
            }
        }

        sb.append(BuildParameterPattern(command));

        sb.insert(0, "^");
        sb.append("$");

        return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
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

    public static String BuildUsage(CommandStructure command) {
        StringBuilder sb = new StringBuilder();
        List<CommandStructure> subCommands = command.getSubcommands();

        sb.append(command.getName());

        if(subCommands.size() > 0) {
            boolean anySubCommandsRequired = subCommands.stream().anyMatch(sc -> !sc.isOptional());

            sb.append(" ");

            if(subCommands.size() > 1) {
                if (anySubCommandsRequired) {
                    sb.append("<");
                } else {
                    sb.append("[");
                }

                sb.append("[");
            }

            sb.append(buildSubCommandUsageLabel(subCommands));

            if(subCommands.size() > 1) {
                sb.append("]");

                if (anySubCommandsRequired) {
                    sb.append(">");
                } else {
                    sb.append("]");
                }
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
