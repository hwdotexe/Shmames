package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Utils;

public class IsDevAlive implements ICommand {
	@Override
	public String getDescription() {
		return "Check if the Developer is alive.";
	}
	
	@Override
	public String getUsage() {
		return "is the dev alive";
	}

	@Override
	public String run(String args, User author, Message message) {
		int r = Integer.parseInt(Utils.sendGET("http://db.hadenw.tech:8080/JamesAPI/james/life/get?p=KeepDevAlive34"));
		
		if(r <= 20) {
			return "Cynical should probably get an ambulence ("+r+"bpm)";
		}else if(r > 20 && r <= 50) {
			return "He's... okay... ("+r+"bpm)";
		}else if(r > 50 && r < 120) {
			return "He seems to be in good health ("+r+"bpm)";
		}else {
			return "Maybe you should check up on him... ("+r+"bpm)";
		}
	}

	@Override
	public String[] getAliases() {
		return new String[] {"is the dev alive", "is the developer alive", "is cynical dead"};
	}
	
	@Override
	public String sanitize(String i) {
		return i;
	}
	
	@Override
	public boolean requiresGuild() {
		return false;
	}
}
