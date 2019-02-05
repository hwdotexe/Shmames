package tech.hadenw.shmamesbot;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tech.hadenw.shmamesbot.commands.CommandHandler;

public class Chat extends ListenerAdapter {
	private CommandHandler cmd;
	
	public Chat() {
		cmd = new CommandHandler();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (!e.getAuthor().isBot()) {

			String message = e.getMessage().getContentDisplay();
			
			for (String trigger : Shmames.getBrain().getTriggers(TriggerType.COMMAND)) {
				if (message.toLowerCase().startsWith(trigger.toLowerCase())) {
					String command = message.substring(trigger.length()).trim();
					
					System.out.println("[COMMAND/"+e.getAuthor().getName()+"]: "+command);
					cmd.PerformCommand(command, e.getChannel(), e.getAuthor(), e.getGuild());
					
					return;
				}
			}
		}
	}
}
