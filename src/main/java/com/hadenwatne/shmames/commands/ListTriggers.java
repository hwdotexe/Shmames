package com.hadenwatne.shmames.commands;

import com.hadenwatne.shmames.storage.Lang;
import com.hadenwatne.shmames.storage.Langs;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import com.hadenwatne.shmames.Utils;
import com.hadenwatne.shmames.storage.Brain;

import javax.annotation.Nullable;

public class ListTriggers implements ICommand {
	private Lang lang;
	private Brain brain;

	@Override
	public String getDescription() {
		return "Displays all the current message trigger words or phrases, along with their types.";
	}
	
	@Override
	public String getUsage() {
		return "listTriggers";
	}

	@Override
	public String run(String args, User author, Message message) {
		String list = Utils.GenerateList(brain.getTriggers(), -1);

		return lang.getMsg(Langs.TRIGGER_LIST)+"\n"+list;
	}

	@Override
	public String[] getAliases() {
		return new String[] {"listtriggers", "list triggers"};
	}

	@Override
	public void setRunContext(Lang lang, @Nullable Brain brain) {
		this.lang = lang;
		this.brain = brain;
	}
	
	@Override
	public boolean requiresGuild() {
		return true;
	}
}
