package tech.hadenw.shmamesbot.commands;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import tech.hadenw.shmamesbot.Shmames;

public class EightBall implements ICommand {
	@Override
	public String getDescription() {
		return "Shake a Magic 8 Ball and let James decide your future.";
	}

	@Override
	public String run(String args, User author, Guild server) {
		String[] answers = new String[] {"It is certain.", "It is decidedly so.","Without a doubt.","Yes - definitely.",
				"You may rely on it.","As I see it, yes.","Most likely.","Outlook good.","Yes.","Signs point to yes.",
				"Why don't you ask me later?","Don't count on it.","My reply is no.","My sources say no.","Outlook not so good.","Very doubtful."};
		
		return answers[Shmames.getRandom(answers.length)];
	}

	@Override
	public String[] getAliases() {
		return new String[] {"8ball", "should", "will", "can"};
	}
}
