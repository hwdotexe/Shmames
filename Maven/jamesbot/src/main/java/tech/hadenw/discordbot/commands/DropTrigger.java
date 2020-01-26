package tech.hadenw.discordbot.commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import tech.hadenw.discordbot.Shmames;
import tech.hadenw.discordbot.brain.Brain;

public class DropTrigger implements ICommand {
	@Override
	public String getDescription() {
		return "Removes an existing trigger from the bot.";
	}
	
	@Override
	public String getUsage() {
		return "removeTrigger <triggerWord>";
	}

	@Override
	public String run(String args, User author, Message message) {
		if(args.length() > 0) {
			if (!args.equalsIgnoreCase(Shmames.getJDA().getSelfUser().getName().toLowerCase())) {
				Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
				
				if (b.getTriggers().containsKey(args)) {
					b.getTriggers().remove(args);
					Shmames.getBrains().saveBrain(b);
	
					return "I threw it on the **GROUND**!";
				} else
					return "While I'd love to do that, it doesn't exist in my brain, so, I can't...";
			} else {
				return "Sorry, ya can't delete that trigger. It's stuck here like the gum on my shoe #ThanksCarly";
			}
		}else {
			return "Hey! You gotta tell me what you want to delete...";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"droptrigger", "drop trigger", "remove trigger"};
	}
	
	@Override
	public String sanitize(String i) {
		return i.toLowerCase();
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
