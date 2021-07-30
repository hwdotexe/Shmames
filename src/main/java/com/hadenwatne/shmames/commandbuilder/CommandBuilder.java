package com.hadenwatne.shmames.commandbuilder;

import com.hadenwatne.shmames.commands.ICommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.regex.Pattern;

public class CommandBuilder {
    public static CommandStructure Create(String name) {
        return new CommandStructure(name);
    }

    public static CommandData BuildCommandData(ICommand command) {
        CommandStructure structure = command.getCommandStructure();
        CommandData data = new CommandData(structure.getName(), command.getDescription());

        for(CommandParameter p : structure.getParameters()) {
            OptionData option = new OptionData(MapParameterType(p.getType()), p.getName().toLowerCase(), p.getDescription())
                .setRequired(p.isRequired());

            if(p.getSelectionOptions().size() > 0) {
                for(String so : p.getSelectionOptions()) {
                    option.addChoice(so, so);
                }
            }

            data.addOptions(option);
        }

        return data;
    }

    public static Pattern BuildPattern(CommandStructure command) {
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
        }

        sb.insert(0, "^");
        sb.append("$");

        return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
    }

    public static String BuildUsage(CommandStructure command) {
        StringBuilder sb = new StringBuilder();

        sb.append(command.getName());

        for(CommandParameter p : command.getParameters()) {
            sb.append(" ");

            if(p.isRequired()) {
                sb.append("<");
                sb.append(buildUsageLabel(p));
                sb.append(">");
            }else{
                sb.append("[");
                sb.append(buildUsageLabel(p));
                sb.append("]");
            }
        }

        return sb.toString();
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
