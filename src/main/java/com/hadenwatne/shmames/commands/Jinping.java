package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.storage.Lang;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Shmames;
import com.hadenwatne.shmames.storage.Brain;
import com.hadenwatne.shmames.tasks.JinpingTask;

import javax.annotation.Nullable;

public class Jinping implements ICommand {
	@Override
	public String getDescription() {
		return "Spam :ping_pong: for one minute in support of the Hong Kong pro-democracy protesters.";
	}
	
	@Override
	public String getUsage() {
		return "jinping";
	}

	@Override
	public String run(String args, User author, Message message) {
		Brain b = Shmames.getBrains().getBrain(message.getGuild().getId());
		
		new JinpingTask(b);
		
		return "SPAM :ping_pong: THIS :ping_pong: PONG :ping_pong: TO :ping_pong: FREE :ping_pong: HONG :ping_pong: KONG";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"jinping"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {

	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
