package com.hadenwatne.shmames.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hadenwatne.shmames.CommandHandler;
import com.hadenwatne.shmames.models.Brain;
import com.hadenwatne.shmames.models.Lang;
import com.hadenwatne.shmames.enums.Langs;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.enums.Errors;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.Utils;

import javax.annotation.Nullable;

public class Help implements ICommand {
	private Lang lang;

	@Override
	public String getDescription() {
		return "Shows help & additional information.";
	}
	
	@Override
	public String getUsage() {
		return "help [command]";
	}

	@Override
	public String getExamples() {
		return "`help`\n" +
				"`help gif`";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0) {
			// Wants help on specific command.
			
			for(ICommand c : CommandHandler.getLoadedCommands()) {
				for(String a : c.getAliases()) {
					if(a.equalsIgnoreCase(args)) {
						// Create list of aliases
						String list = Utils.generateList(Arrays.asList(c.getAliases()), -1, false, false);
						 
						EmbedBuilder eBuilder = new EmbedBuilder();
						
						eBuilder.setAuthor("Help » "+c.getAliases()[0]);
						eBuilder.setColor(Color.MAGENTA);
						eBuilder.addField("Description", c.getDescription(), false);
						eBuilder.addField("Aliases", list, true);
						eBuilder.addField("Server-only", c.requiresGuild() ? "Yes" : "No", true);
						eBuilder.addField("Usage", c.getUsage(), false);
						eBuilder.addField("Examples", c.getExamples(), false);
						
				        message.getChannel().sendMessage(eBuilder.build()).queue();
				        
						return "";
					}
				}
			}
		}else {
			// Wants a list of all commands.
			List<String> cmds = new ArrayList<String>();

			for(ICommand c : CommandHandler.getLoadedCommands()) {
				if(c.getDescription().length() > 0) {
					cmds.add(c.getAliases()[0]);
				}
			}

			String list = Utils.generateList(cmds, -1, false, false);

			EmbedBuilder eBuilder = new EmbedBuilder();

			eBuilder.setColor(Color.MAGENTA);
			eBuilder.setTitle("Command Help for "+Shmames.getBotName());
			eBuilder.addField("All Commands", list, false);
			eBuilder.addField("Information", "View additional information for each command by using `"+Shmames.getBotName()+" help <command>`!", false);

			if(message.getChannelType() == ChannelType.TEXT){
				author.openPrivateChannel().queue((c) -> c.sendMessage(eBuilder.build()).queue());

				return lang.getMsg(Langs.SENT_PRIVATE_MESSAGE);
			}else{
				message.getChannel().sendMessage(eBuilder.build()).queue();

				return "";
			}
		}

		return lang.getError(Errors.COMMAND_NOT_FOUND, true);
	}

	@Override
	public String[] getAliases() {
		return new String[] {"help", "how do you use", "how do I use"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
